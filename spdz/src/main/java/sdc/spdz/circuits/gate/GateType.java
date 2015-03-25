package sdc.spdz.circuits.gate;

import java.util.Random;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public enum GateType {

   PLUS, MULT;

   public static GateType getRandomGate() {
      Random random = new Random();
      GateType gate = random.nextBoolean() ? GateType.PLUS : GateType.MULT;

      return gate;
   }
}
