package sdc.spdz.circuits;

import java.util.ArrayList;
import java.util.logging.Logger;
import sdc.spdz.circuits.exception.UnknownExecutionModeException;
import sdc.spdz.circuits.player.Player;
import sdc.spdz.circuits.player.PlayerID;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class MainThree {

   private static final Logger logger = Logger.getLogger(MainThree.class.getName());

   public static void main(String[] args) throws UnknownExecutionModeException, InterruptedException {
      int P = 41;
      int NINPUTS = 100;
      int NPLAYERS = 7;
      Group group = new Group(P);

      // generate a random circuit
      CircuitGenerator generator = new CircuitGenerator();
      Circuit circuit = generator.generate(NINPUTS);

      System.out.println(circuit.toString());

      // generate random inputs for the circuit
      int[] inputs = new int[NINPUTS];
      for (int i = 0; i < NINPUTS; i++) {
         inputs[i] = group.random();
      }

      System.out.println("SINGLE-PARTY:");
      // run the circuit with only one player
      Player singlePlayer = new Player("", "", 0);
      singlePlayer.setCircuit(circuit);
      singlePlayer.setExecutionMode(ExecutionMode.LOCAL);
      singlePlayer.setMOD(P);
      singlePlayer.setInputs(inputs);
      singlePlayer.start();
      singlePlayer.join();
      System.out.println("MULTI-PARTY");
      // create shares for all the circuit's inputs
      // number of shares == number of players
      Inputs[] inputShares = new Inputs[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         inputShares[i] = new Inputs(NINPUTS);
      }

      for (int i = 0; i < NINPUTS; i++) {
         int[] shares = group.createShares(inputs[i], NPLAYERS);
         for (int j = 0; j < NPLAYERS; j++) {
            inputShares[j].add(shares[j]);
         }
      }
      
      // create random multiplication triples for all multiplication gates
      int numberOfMultiplications = circuit.getMultiplicationGatesCount();
      MultiplicationTriple[] multiplicationTriples = new MultiplicationTriple[numberOfMultiplications];
      for (int i = 0; i < numberOfMultiplications; i++) {
         multiplicationTriples[i] = group.randomMultiplicationTriple();
      }

      // init all pre processed data
      PreProcessedData[] preProcessedData = new PreProcessedData[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         preProcessedData[i] = new PreProcessedData();
      }

      // create shares for all multiplication triples previously generated
      for (int i = 0; i < numberOfMultiplications; i++) {
         int[] aShares = group.createShares(multiplicationTriples[i].getA(), NPLAYERS);
         int[] bShares = group.createShares(multiplicationTriples[i].getB(), NPLAYERS);
         int[] cShares = group.createShares(multiplicationTriples[i].getC(), NPLAYERS);
         for (int j = 0; j < NPLAYERS; j++) {
            preProcessedData[j].add(new MultiplicationTriple(aShares[j], bShares[j], cShares[j]));
         }
      }

      Player[] players = new Player[NPLAYERS];
      ArrayList<PlayerID> playersID = new ArrayList();
      for (int i = 0; i < NPLAYERS; i++) {
         players[i] = new Player("UID" + i, "localhost", 3000 + i);
         playersID.add(players[i].getID());
      }

      for (int i = 0; i < NPLAYERS; i++) {
         players[i].setCircuit(circuit);
         players[i].setMOD(P);

         players[i].setInputs(inputShares[i].get());
         players[i].setPreProcessedData(preProcessedData[i]);

         players[i].setExecutionMode(ExecutionMode.DISTRIBUTED);

         ArrayList<PlayerID> playersIDCopy = new ArrayList(playersID);
         playersIDCopy.remove(players[i].getID());
         players[i].setPlayers(playersIDCopy);
      }

      for (int i = 0; i < NPLAYERS; i++) {
         players[i].start();
      }
      for (int i = 0; i < NPLAYERS; i++) {
         players[i].join();
      }

   }
}
