package sdc.spdz.circuits.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import sdc.spdz.circuits.exception.UnknownExecutionModeException;
import sdc.spdz.circuits.exception.ParamNotFoundException;
import sdc.spdz.circuits.exception.InvalidPlayersException;
import sdc.spdz.circuits.Circuit;
import sdc.spdz.circuits.CircuitTriple;
import sdc.spdz.circuits.ExecutionMode;
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
public class Player {

   private static final Logger logger = Logger.getLogger(Player.class.getName());
   private final String MESSAGE_SEPARATOR = "::";

   private final PlayerID playerID;
   private Circuit circuit;
   private int[] inputs;
   private Integer MOD;
   private PlayerID[] players;
   private PreProcessedData preProcessedData;

   public Player(String host, int port) {
      this.playerID = new PlayerID(host, port);
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

   public void setPlayers(PlayerID... otherPlayers) {
      this.players = otherPlayers;
   }

   public int evalCircuit(ExecutionMode executionMode) throws ParamNotFoundException, InvalidParamNumberException, InvalidPlayersException, UnknownOperationException, UnknownExecutionModeException {
      checkParams();
      if (executionMode.equals(ExecutionMode.DISTRIBUTED)) {
         checkPreProcessedData();
         checkPlayers();
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
                     result = 0;
                     //result = evalDistributedMult(params[0], params[1], preProcessedData.consume(), countDistributedMultiplications++);
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

      return edgesValues[edgesValues.length - 1];
   }

   private void checkParams() throws ParamNotFoundException {
      if (circuit == null) {
         throw new ParamNotFoundException("Circuit Not Found");
      }
      if (inputs == null) {
         throw new ParamNotFoundException("Inputs Not Found");
      }
      if (MOD == null) {
         throw new ParamNotFoundException("MOD Not Found");
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
            throw new InvalidPlayersException();
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
/*
   private int evalDistributedMult(int x, int y, MultiplicationTriple mt, int countDistributedMultiplications) {
      int dShared = (x - mt.getA()) % MOD;
      int eShared = (y - mt.getB()) % MOD;

      String message = countDistributedMultiplications + MESSAGE_SEPARATOR 
              + playerID.getUID() + MESSAGE_SEPARATOR 
              + dShared + MESSAGE_SEPARATOR 
              + eShared + "\n";
      //count::uid_i::d_i::e_i

   }

   private class SocketReader extends Thread {

      private Map<Integer, Pair<Integer, Integer>> mapGateToShares = new HashMap();
      
      @Override
      public void run() {
         try {
            Socket socket = new Socket(playerID.getHost(), playerID.getPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
               String[] parts = line.split(MESSAGE_SEPARATOR);
               if(mapGateToShares.containsKey(parts[0])){
                  mapGateToShares.get(parts[0]).add(MOD)
               }
            }
         } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
         }
      }
   }
*/
}
