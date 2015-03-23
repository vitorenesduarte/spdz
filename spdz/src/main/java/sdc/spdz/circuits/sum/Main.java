package sdc.spdz.circuits.sum;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.spdz.circuits.Circuit;
import sdc.spdz.circuits.CircuitGenerator;
import sdc.spdz.circuits.operation.InvalidParamNumberException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Main {

   public static void main(String[] args) {
      try {
         int P = 41;
         int NINPUTS = 41;
         Group group = new Group(P);
         CircuitGenerator generator = new CircuitGenerator(P);
         Circuit circuit = generator.generate(NINPUTS);

         int[] inputs = new int[NINPUTS];
         int inputSum = 0;
         for (int i = 0; i < NINPUTS; i++) {
            Random random = new Random();
            inputs[i] = random.nextInt(P);
            inputSum += inputs[i];
         }
         System.out.println("INPUT : " + inputSum);

         int[][] inputShares = new int[NINPUTS][NINPUTS];
         for (int i = 0; i < NINPUTS; i++) {
            inputShares[i] = group.createShares(inputs[i], NINPUTS);
         }

         int resultSum = 0;
         for (int i = 0; i < NINPUTS; i++) {
            circuit.init(inputShares[i]);
            circuit.run();
            resultSum += circuit.getResult();
         }
         System.out.println("RESULT : " + resultSum);

      } catch (InvalidParamNumberException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   static class Worker extends Thread {

      private int[] inputs;
      private final Circuit circuit;

      public Worker(Circuit c, int... inputs) {
         this.circuit = c;
         this.inputs = inputs.clone();
      }

      @Override
      public void run() {
         try {
            circuit.init(inputs);
            circuit.run();
            System.out.println(circuit.getResult());

         } catch (InvalidParamNumberException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
}
