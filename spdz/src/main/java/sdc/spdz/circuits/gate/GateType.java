package sdc.spdz.circuits.gate;

import java.security.SecureRandom;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public enum GateType {

   PLUS, MULT;

   private static final SecureRandom random = new SecureRandom();

   public static GateType getRandomGate() {
      GateType gate = random.nextBoolean() ? GateType.PLUS : GateType.MULT;
      return gate;
   }
}
