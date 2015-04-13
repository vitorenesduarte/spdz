package sdc.avoidingproblems.circuits.player;

import sdc.avoidingproblems.circuits.algebra.Share;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidPlayersException;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.ExecutionMode;
import static sdc.avoidingproblems.circuits.ExecutionMode.DISTRIBUTED;
import static sdc.avoidingproblems.circuits.ExecutionMode.LOCAL;
import sdc.avoidingproblems.circuits.Gate;
import sdc.avoidingproblems.circuits.GateSemantic;
import static sdc.avoidingproblems.circuits.GateSemantic.MULT;
import static sdc.avoidingproblems.circuits.GateSemantic.PLUS;
import sdc.avoidingproblems.circuits.PreProcessedData;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.OperationNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Player extends Thread {

   private static final Logger logger = Logger.getLogger(Player.class.getName());
   private final String MESSAGE_SEPARATOR = "::";
   private int countDistributedMultiplications = 0;
   private final Semaphore semaphore = new Semaphore(0);
   private final List<Share> sharesReady = new ArrayList();

   private final PlayerID playerID;
   private Circuit circuit;
   private List<FieldElement> inputs;
   private Integer MOD;
   private List<PlayerID> players;
   private PreProcessedData preProcessedData;
   private ExecutionMode executionMode;
   private final int UID;
   private final List<FieldElement> sumAll;

   public Player(int UID, String host, int port, List<FieldElement> sumAll) {
      this.UID = UID;
      this.playerID = new PlayerID("UID" + UID, host, port);
      this.sumAll = sumAll;
   }

   public PlayerID getID() {
      return playerID;
   }

   public void setCircuit(Circuit circuit) {
      this.circuit = circuit;
   }

   public void setInputs(List<FieldElement> inputs) {
      this.inputs = inputs;
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

         List<Gate> gates = circuit.getGates();
         List<FieldElement> edgesValues = initEdgesValues();

         for (Gate gate : gates) {
            List<Integer> inputEdges = gate.getInputEdges();
            FieldElement[] params = new FieldElement[inputEdges.size()];
            for (int j = 0; j < inputEdges.size(); j++) {
               params[j] = edgesValues.get(inputEdges.get(j));
            }

            FieldElement result;
            GateSemantic semantic = gate.getSemantic();

            switch (semantic) {
               case MULT:
                  switch (executionMode) {
                     case LOCAL:
                        result = GateSemantic.getFunction(semantic).apply(LOCAL, params);
                        break;
                     case DISTRIBUTED:
                        result = evalDistributedMult(params[0], params[1], preProcessedData.consume(), countDistributedMultiplications++);
                        break;
                     default:
                        throw new ExecutionModeNotSupportedException();
                  }
                  break;
               case PLUS:
                  result = GateSemantic.getFunction(semantic).apply(LOCAL, params);
                  break;
               default:
                  throw new OperationNotSupportedException();
            }
            edgesValues.add(result);
         }

         FieldElement result = edgesValues.get(edgesValues.size() - 1);
         if (executionMode == LOCAL) {
            System.out.println("RESULT: " + result.intValue());
         } else {
            sumAll.add(result);
         }
      } catch (InvalidParamException | InvalidPlayersException | InterruptedException | ExecutionModeNotSupportedException | OperationNotSupportedException ex) {
         logger.log(Level.SEVERE, null, ex);
      }
   }

   private void checkParams() throws InvalidParamException {
      if (circuit == null) {
         throw new InvalidParamException("Circuit Not Found");
      }
      if (inputs == null) {
         throw new InvalidParamException("Inputs Not Found");
      }
      if (circuit.getInputSize() != inputs.size()) {
         throw new InvalidParamException("Circuit's number of inputs is different from inputs lenght");
      }
      if (MOD == null) {
         throw new InvalidParamException("MOD Not Found");
      }
      if (executionMode == null) {
         throw new InvalidParamException("Execution Mode Not Found");
      }
   }

   private void checkPreProcessedData() throws InvalidParamException {
      if (preProcessedData == null) {
         throw new InvalidParamException("Pre Processed Data Not Found");
      }
   }

   private void checkPlayers() throws InvalidPlayersException, InvalidParamException {
      if (players == null) {
         throw new InvalidParamException("Players Not Found");
      }
      for (PlayerID pID : players) {
         if (pID.equals(playerID)) {
            throw new InvalidPlayersException(); // I cannot multi party with myself
         }
      }
   }

   private List<FieldElement> initEdgesValues() {
      List<FieldElement> edgesValues = new ArrayList(inputs.size() + circuit.getGateCount());
      for (FieldElement fe : inputs) {
         edgesValues.add(fe);
      }
      return edgesValues;
   }

   private FieldElement evalDistributedMult(FieldElement x, FieldElement y, BeaverTriple triple, int countDistributedMultiplications) throws InterruptedException, InvalidParamException, ExecutionModeNotSupportedException {
      FieldElement dShared = x.sub(triple.getA()); // x is now equals to dShared
      FieldElement eShared = y.sub(triple.getB()); // y is now equals to eShared

      String message = countDistributedMultiplications + MESSAGE_SEPARATOR
              + playerID.getUID() + MESSAGE_SEPARATOR
              + dShared.intValue() + MESSAGE_SEPARATOR
              + eShared.intValue() + "\n";
      //count::uid_i::d_i::e_i

      sendToPlayers(message);

      semaphore.acquire();
      Share readyShare = sharesReady.remove(0);
      readyShare.addToD(dShared.intValue());
      readyShare.addToE(eShared.intValue());

      Function f = GateSemantic.getFunction(MULT);
      f.setBeaverTriple(triple);
      FieldElement result = f.apply(DISTRIBUTED, dShared, readyShare.getD(), readyShare.getE());
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
                     Share tuple = new Share(dShare, eShare, MOD);
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
