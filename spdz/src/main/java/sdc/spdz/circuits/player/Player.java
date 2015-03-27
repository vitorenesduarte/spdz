package sdc.spdz.circuits.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.spdz.circuits.exception.UnknownExecutionModeException;
import sdc.spdz.circuits.exception.ParamNotFoundException;
import sdc.spdz.circuits.exception.InvalidPlayersException;
import sdc.spdz.circuits.Circuit;
import sdc.spdz.circuits.CircuitTriple;
import sdc.spdz.circuits.ExecutionMode;
import static sdc.spdz.circuits.ExecutionMode.LOCAL;
import sdc.spdz.circuits.MultiplicationTriple;
import sdc.spdz.circuits.PreProcessedData;
import sdc.spdz.circuits.exception.InvalidParamNumberException;
import sdc.spdz.circuits.exception.UnknownOperationException;
import sdc.spdz.circuits.gate.Mult;
import sdc.spdz.circuits.gate.Plus;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Player extends Thread {

   private static final Logger logger = Logger.getLogger(Player.class.getName());
   private final String MESSAGE_SEPARATOR = "::";

   private final Semaphore semaphore;
   private final ArrayList<Share> sharesReady;

   private final PlayerID playerID;
   private Circuit circuit;
   private int[] inputs;
   private Integer MOD;
   private ArrayList<PlayerID> players;
   private PreProcessedData preProcessedData;
   private ExecutionMode executionMode;
   private final int UID;
   private final int[] sumAll;

   public Player(int UID, String host, int port, int[] sumAll) {
      this.UID = UID;
      this.playerID = new PlayerID("UID" + UID, host, port);
      sharesReady = new ArrayList<>();
      semaphore = new Semaphore(0);
      this.sumAll = sumAll;
   }

   public PlayerID getID() {
      return playerID;
   }

   public void setCircuit(Circuit circuit) {
      this.circuit = circuit;
   }

   public void setInputs(int... inputs) {
      this.inputs = inputs.clone();
   }

   public void setMOD(int MOD) {
      this.MOD = MOD;
   }

   public void setPreProcessedData(PreProcessedData preProcessedData) {
      this.preProcessedData = preProcessedData;
   }

   public void setPlayers(ArrayList<PlayerID> otherPlayers) {
      this.players = otherPlayers;
   }

   public void setExecutionMode(ExecutionMode executionMode) {
      this.executionMode = executionMode;
   }

   @Override
   public void run() {
      try {
         checkParams();
         if (executionMode.equals(ExecutionMode.DISTRIBUTED)) {
            checkPreProcessedData();
            checkPlayers();
            SocketReader reader = new SocketReader();
            reader.start();
            Thread.sleep(1000);
         }

         CircuitTriple[] triples = circuit.getTriples();
         int[] edgesValues = initEdgesValues();
         Mult mult = new Mult(MOD);
         Plus plus = new Plus(MOD);
         int countDistributedMultiplications = 0;

         for (int i = 0; i < triples.length; i++) {
            CircuitTriple ct = triples[i];
            int[] gateInputs = ct.getGateInputs();
            int[] params = new int[gateInputs.length];
            for (int j = 0; j < gateInputs.length; j++) {
               params[j] = edgesValues[gateInputs[j]];
            }
            int result;

            switch (ct.getGate()) {
               case MULT:
                  switch (executionMode) {
                     case LOCAL:
                        result = mult.eval(params[0], params[1]);
                        break;
                     case DISTRIBUTED:
                        result = evalDistributedMult(params[0], params[1], mult, preProcessedData.consume(), countDistributedMultiplications++);
                        break;
                     default:
                        throw new UnknownExecutionModeException();
                  }
                  break;
               case PLUS:
                  result = plus.eval(params[0], params[1]);
                  break;
               default:
                  throw new UnknownOperationException();
            }

            edgesValues[inputs.length + i] = result;
         }

         int result = edgesValues[edgesValues.length - 1];
         if (executionMode == LOCAL) {
            System.out.println(result);
         } else {
            sumAll[UID] = result;
         }
      } catch (InvalidParamNumberException | InvalidPlayersException | ParamNotFoundException | InterruptedException | UnknownExecutionModeException | UnknownOperationException ex) {
         logger.log(Level.SEVERE, null, ex);
      }
   }

   private void checkParams() throws ParamNotFoundException, InvalidParamNumberException {
      if (circuit == null) {
         throw new ParamNotFoundException("Circuit Not Found");
      }
      if (inputs == null) {
         throw new ParamNotFoundException("Inputs Not Found");
      }
      if (circuit.getInputSize() != inputs.length) {
         throw new InvalidParamNumberException("Circuit's number of inputs is different from inputs lenght");
      }
      if (MOD == null) {
         throw new ParamNotFoundException("MOD Not Found");
      }
      if (executionMode == null) {
         throw new ParamNotFoundException("Execution Mode Not Found");
      }
   }

   private void checkPreProcessedData() throws ParamNotFoundException {
      if (preProcessedData == null) {
         throw new ParamNotFoundException("Pre Processed Data Not Found");
      }
   }

   private void checkPlayers() throws InvalidPlayersException, ParamNotFoundException {
      if (players == null) {
         throw new ParamNotFoundException("Players Not Found");
      }
      for (PlayerID pID : players) {
         if (pID.equals(playerID)) {
            throw new InvalidPlayersException(); // I cannot multi party with myself
         }
      }
   }

   private int[] initEdgesValues() {
      int[] edgesValues = new int[inputs.length + circuit.getTriplesCount()];
      System.arraycopy(inputs, 0, edgesValues, 0, inputs.length);
      // same as:
      /*
       for (int i = 0; i < inputs.length; i++) {
       edgesValues[i] = inputs[i];
       }
       */
      return edgesValues;
   }

   private int evalDistributedMult(int x, int y, Mult mult, MultiplicationTriple mt, int countDistributedMultiplications) throws InterruptedException {
      int dShared = (x - mt.getA()) % MOD;
      if (dShared < 0) {
         dShared += MOD;
      }
      int eShared = (y - mt.getB()) % MOD;
      if (eShared < 0) {
         eShared += MOD;
      }

      String message = countDistributedMultiplications + MESSAGE_SEPARATOR
              + playerID.getUID() + MESSAGE_SEPARATOR
              + dShared + MESSAGE_SEPARATOR
              + eShared + "\n";
      //count::uid_i::d_i::e_i

      sendToPlayers(message);

      semaphore.acquire();
      Share readyShare = sharesReady.remove(0);
      int dPublic = (readyShare.getD() + dShared) % MOD;
      int ePublic = (readyShare.getE() + eShared) % MOD;

      int result = mult.evalDistributed(dShared, dPublic, ePublic, mt);
      return result;
   }

   private void sendToPlayers(String message) {
      try {
         for (PlayerID pid : players) {
            Socket socket = new Socket(pid.getHost(), pid.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.write(message);
            //out("mandei para " + pid.getUID() + " : " + message);
            out.flush();
            out.close();
            socket.close();
         }
      } catch (IOException ex) {
         logger.info(message);
         logger.log(Level.SEVERE, null, ex);
      }
   }

   private class SocketReader extends Thread {

      private final Map<Integer, Share> mapGateToShares;

      private SocketReader() {
         this.mapGateToShares = new HashMap();
      }

      @Override
      public void run() {
         try {
            ServerSocket ss = new ServerSocket(playerID.getPort());
            while (true) {
               Socket socket = ss.accept();
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               String line = in.readLine();
               //out("recebi : " + line);
               String[] parts = line != null ? line.split(MESSAGE_SEPARATOR) : null;
               if (parts != null && parts.length == 4) {
                  int mult = Integer.valueOf(parts[0]);
                  int dShare = Integer.valueOf(parts[2]);
                  int eShare = Integer.valueOf(parts[3]);
                  if (mapGateToShares.containsKey(mult)) {
                     Share share = mapGateToShares.get(mult);
                     share.addToD(dShare);
                     share.addToE(eShare);
                     share.incrNumberOfShares();
                  } else {
                     Share tuple = new Share(dShare, eShare);
                     mapGateToShares.put(mult, tuple);
                  }

                  if (mapGateToShares.get(mult).getNumberOfShares() == players.size()) {
                     sharesReady.add(mapGateToShares.remove(mult));
                     semaphore.release();
                  }

               }
               in.close();
               socket.close();
            }
         } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
         }
      }
   }

   public void out(String s) {
      System.out.println(playerID.getUID() + " -> " + s);
   }
}
