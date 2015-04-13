package sdc.avoidingproblems.circuits.player;

import sdc.avoidingproblems.circuits.algebra.DsAndEs;
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
import static sdc.avoidingproblems.circuits.ExecutionMode.DISTRIBUTED;
import static sdc.avoidingproblems.circuits.ExecutionMode.LOCAL;
import sdc.avoidingproblems.circuits.Gate;
import sdc.avoidingproblems.circuits.GateSemantic;
import static sdc.avoidingproblems.circuits.GateSemantic.MULT;
import static sdc.avoidingproblems.circuits.GateSemantic.PLUS;
import sdc.avoidingproblems.circuits.PreProcessedData;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.algebra.ValueAndMAC;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
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
   private final List<DsAndEs> sharesReady = new ArrayList();

   private final PlayerID playerID;
   private Circuit circuit;
   private List<ValueAndMAC> sharedInputs;
   private Integer MOD;
   private List<PlayerID> players;
   private PreProcessedData preProcessedData;
   private final int UID;
   private final List<ValueAndMAC> sumAll;

   public Player(int UID, String host, int port, List<ValueAndMAC> sumAll) {
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

   public void setInputs(List<ValueAndMAC> sharedInputs) {
      this.sharedInputs = sharedInputs;
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

   @Override
   public void run() {
      try {
         checkParams();
         checkPreProcessedData();
         checkPlayers();
         SocketReader reader = new SocketReader();
         reader.start();
         Thread.sleep(1000);

         List<Gate> gates = circuit.getGates();
         List<ValueAndMAC> edgesValues = initEdgesValues();

         for (Gate gate : gates) {
            List<Integer> inputEdges = gate.getInputEdges();
            ValueAndMAC[] params = new ValueAndMAC[inputEdges.size()];
            for (int j = 0; j < inputEdges.size(); j++) {
               params[j] = edgesValues.get(inputEdges.get(j));
            }

            ValueAndMAC result;
            GateSemantic semantic = gate.getSemantic();

            switch (semantic) {
               case MULT:
                  result = evalDistributedMult(params[0], params[1], preProcessedData.consume(), countDistributedMultiplications++);
                  break;
               case PLUS:
                  result = GateSemantic.getFunction(semantic).apply(LOCAL, null, null, null, params);
                  break;
               default:
                  throw new OperationNotSupportedException();
            }
            edgesValues.add(result);
         }

         ValueAndMAC result  = edgesValues.get(edgesValues.size() - 1);
         sumAll.add(result);
      } catch (InvalidParamException | InvalidPlayersException | InterruptedException | ExecutionModeNotSupportedException | OperationNotSupportedException ex) {
         logger.log(Level.SEVERE, null, ex);
      }
   }

   private void checkParams() throws InvalidParamException {
      if (circuit == null) {
         throw new InvalidParamException("Circuit Not Found");
      }
      if (sharedInputs == null) {
         throw new InvalidParamException("Inputs Not Found");
      }
      if (circuit.getInputSize() != sharedInputs.size()) {
         throw new InvalidParamException("Circuit's number of inputs is different from inputs lenght");
      }
      if (MOD == null) {
         throw new InvalidParamException("MOD Not Found");
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

   private List<ValueAndMAC> initEdgesValues() {
      List<ValueAndMAC> edgesValues = new ArrayList(sharedInputs.size() + circuit.getGateCount());
      for (ValueAndMAC vam : sharedInputs) {
         edgesValues.add(vam);
      }
      return edgesValues;
   }

   private ValueAndMAC evalDistributedMult(ValueAndMAC x, ValueAndMAC y, BeaverTriple triple, int countDistributedMultiplications) throws InterruptedException, InvalidParamException, ExecutionModeNotSupportedException {
      ValueAndMAC dShared = x.sub(triple.getA());
      ValueAndMAC eShared = y.sub(triple.getB());

      String message = countDistributedMultiplications + MESSAGE_SEPARATOR
              + playerID.getUID() + MESSAGE_SEPARATOR
              + dShared.getValue().intValue() + MESSAGE_SEPARATOR
              + eShared.getValue().intValue() + "\n";
      //count::uid_i::d_i::e_i

      sendToPlayers(message);

      semaphore.acquire();
      DsAndEs readyShare = sharesReady.remove(0);
      readyShare.addToD(dShared.getValue().intValue());
      readyShare.addToE(eShared.getValue().intValue());

      Function f = GateSemantic.getFunction(MULT);
      ValueAndMAC result = f.apply(DISTRIBUTED, triple, readyShare.getD(), readyShare.getE(), dShared);
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

      private final Map<Integer, DsAndEs> mapGateToShares;

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
                     DsAndEs share = mapGateToShares.get(mult);
                     share.addToD(dShare);
                     share.addToE(eShare);
                     share.incrNumberOfShares();
                  } else {
                     DsAndEs tuple = new DsAndEs(dShare, eShare, MOD, sharedInputs.get(0).getValue().getClass());
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
         } catch (IOException | ClassNotSupportedException ex) {
            logger.log(Level.SEVERE, null, ex);
         }
      }
   }

   public void out(String s) {
      System.out.println(playerID.getUID() + " -> " + s);
   }
}
