package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.CircuitGenerator;
import sdc.avoidingproblems.circuits.algebra.Field;
import sdc.avoidingproblems.circuits.PreProcessedData;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.BigIntegerFE;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.ValueAndMAC;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Main {

   public static void main(String[] args) throws ExecutionModeNotSupportedException, InterruptedException, ClassNotSupportedException, InvalidParamException {
      int MOD = 41;
      int NINPUTS = 2;
      int NPLAYERS = 2;
      Field field = new Field(MOD);
      Class<?> clazz = BigIntegerFE.class;
      FieldElement fixedMACKey = field.random(clazz);

      List<ValueAndMAC> sumAll = new ArrayList(NPLAYERS);

      // generate a random circuit
      CircuitGenerator generator = new CircuitGenerator();
      Circuit circuit = generator.generate(NINPUTS);

      //Jung.preview(circuit);
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
      System.out.println("FIXED MAC KEY: " + fixedMACKey);
      System.out.println("MOD " + MOD);
      System.out.println("SINGLE-PARTY:");
      FieldElement singePartyEvalResult = circuit.eval(inputs);
      System.out.println("RESULT: (" + singePartyEvalResult + ", " + singePartyEvalResult.mult(fixedMACKey) + ")");

      System.out.println("MULTI-PARTY:");
      // create shares for all the circuit's inputs
      // number of shares == number of players
      SharedInputs[] inputShares = new SharedInputs[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         inputShares[i] = new SharedInputs(NINPUTS);
      }

      for (int i = 0; i < NINPUTS; i++) {
         ValueAndMAC[] shares = field.createShares(new ValueAndMAC(inputs.get(i), fixedMACKey), NPLAYERS);
         for (int j = 0; j < NPLAYERS; j++) {
            inputShares[j].add(shares[j]);
         }
      }

      // create random multiplication triples for all multiplication gates
      int numberOfMultiplications = circuit.getMultiplicationGatesCount();
      BeaverTriple[] multiplicationTriples = new BeaverTriple[numberOfMultiplications];
      for (int i = 0; i < numberOfMultiplications; i++) {
         multiplicationTriples[i] = field.randomMultiplicationTriple(clazz, fixedMACKey);
      }

      // init all pre processed data
      PreProcessedData[] preProcessedData = new PreProcessedData[NPLAYERS];
      for (int i = 0; i < NPLAYERS; i++) {
         preProcessedData[i] = new PreProcessedData();
      }

      // create shares for all multiplication triples previously generated
      for (int i = 0; i < numberOfMultiplications; i++) {
         ValueAndMAC[] aShares = field.createShares(multiplicationTriples[i].getA(), NPLAYERS);
         ValueAndMAC[] bShares = field.createShares(multiplicationTriples[i].getB(), NPLAYERS);
         ValueAndMAC[] cShares = field.createShares(multiplicationTriples[i].getC(), NPLAYERS);
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

      ValueAndMAC all = sumAll.get(0);
      for (int i = 1; i < NPLAYERS; i++) {
         all = all.add(sumAll.get(i));
      }

      System.out.println("RESULT: (" + all.getValue() + ", " + all.getMAC() + ")");
      System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - start));
   }
}
