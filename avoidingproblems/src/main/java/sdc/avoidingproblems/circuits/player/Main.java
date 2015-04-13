package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.CircuitGenerator;
import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.algebra.Field;
import sdc.avoidingproblems.circuits.PreProcessedData;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.BigIntegerFE;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Main {

   public static void main(String[] args) throws ExecutionModeNotSupportedException, InterruptedException, ClassNotSupportedException {
      int MOD = 41;
      int NINPUTS = 10;
      int NPLAYERS = 3;
      Field field = new Field(MOD);
      Class<?> clazz = BigIntegerFE.class;

      List<FieldElement> sumAll = new ArrayList(NPLAYERS);

      // generate a random circuit
      CircuitGenerator generator = new CircuitGenerator();
      Circuit circuit = generator.generate(NINPUTS);

      System.out.println(circuit.toString());
      int numberOfCommunications = NPLAYERS * (NPLAYERS - 1) * circuit.getMultiplicationGatesCount();
      System.out.println("Number of comunications : " + numberOfCommunications);
      System.out.println("Number of players : " + NPLAYERS);
      // generate random inputs for the circuit
      List<FieldElement> inputs = new ArrayList(NINPUTS);
      for (int i = 0; i < NINPUTS; i++) {
         inputs.add(field.random(clazz));
      }

      System.out.println("INPUTS: " + inputs.toString());
      System.out.println("MOD " + MOD);
      System.out.println("SINGLE-PARTY:");
      // run the circuit with only one player
      Player singlePlayer = new Player(0, "", 0, null);
      singlePlayer.setCircuit(circuit);
      singlePlayer.setExecutionMode(ExecutionMode.LOCAL);
      singlePlayer.setMOD(MOD);
      singlePlayer.setInputs(inputs);
      singlePlayer.start();
      singlePlayer.join();
      System.out.println("MULTI-PARTY:");
      // create shares for all the circuit's inputs
      // number of shares == number of players
      Inputs[] inputShares = new Inputs[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         inputShares[i] = new Inputs(NINPUTS);
      }

      for (int i = 0; i < NINPUTS; i++) {
         FieldElement[] shares = field.createShares(inputs.get(i), NPLAYERS);
         for (int j = 0; j < NPLAYERS; j++) {
            inputShares[j].add(shares[j]);
         }
      }

      // create random multiplication triples for all multiplication gates
      int numberOfMultiplications = circuit.getMultiplicationGatesCount();
      BeaverTriple[] multiplicationTriples = new BeaverTriple[numberOfMultiplications];
      for (int i = 0; i < numberOfMultiplications; i++) {
         multiplicationTriples[i] = field.randomMultiplicationTriple(clazz);
      }

      // init all pre processed data
      PreProcessedData[] preProcessedData = new PreProcessedData[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         preProcessedData[i] = new PreProcessedData();
      }

      // create shares for all multiplication triples previously generated
      for (int i = 0; i < numberOfMultiplications; i++) {
         FieldElement[] aShares = field.createShares(multiplicationTriples[i].getA(), NPLAYERS);
         FieldElement[] bShares = field.createShares(multiplicationTriples[i].getB(), NPLAYERS);
         FieldElement[] cShares = field.createShares(multiplicationTriples[i].getC(), NPLAYERS);
         for (int j = 0; j < NPLAYERS; j++) {
            preProcessedData[j].add(new BeaverTriple(aShares[j], bShares[j], cShares[j]));
         }
      }

      Player[] players = new Player[NPLAYERS];
      ArrayList<PlayerID> playersID = new ArrayList();
      for (int i = 0; i < NPLAYERS; i++) {
         players[i] = new Player(i, "localhost", 3000 + i, sumAll);
         playersID.add(players[i].getID());
      }

      for (int i = 0; i < NPLAYERS; i++) {
         players[i].setCircuit(circuit);
         players[i].setMOD(MOD);

         players[i].setInputs(inputShares[i].get());
         players[i].setPreProcessedData(preProcessedData[i]);

         players[i].setExecutionMode(ExecutionMode.DISTRIBUTED);

         ArrayList<PlayerID> playersIDCopy = new ArrayList(playersID);
         playersIDCopy.remove(players[i].getID());
         players[i].setPlayers(playersIDCopy);
      }

      long start = System.currentTimeMillis();
      for (int i = 0; i < NPLAYERS; i++) {
         players[i].start();
      }
      for (int i = 0; i < NPLAYERS; i++) {
         players[i].join();
      }

      FieldElement count = new BigIntegerFE(0, MOD);
      for (int i = 0; i < NPLAYERS; i++) {
         count = count.add(sumAll.get(i));
      }

      System.out.println("RESULT: " + count.intValue());
      System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - start));
   }
}
