package sdc.spdz.circuits.operation;

import java.util.Random;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public enum Gate {

   PLUS, MULT;

   public static Gate getRandomGate() {
      Random random = new Random();
      Gate gate = random.nextBoolean() ? Gate.PLUS : Gate.MULT;

      return gate;
   }
}
