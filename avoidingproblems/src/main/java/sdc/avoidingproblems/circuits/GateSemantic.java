package sdc.avoidingproblems.circuits;

import java.security.SecureRandom;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.algebra.MultFunction;
import sdc.avoidingproblems.circuits.algebra.PlusFunction;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public enum GateSemantic {

   PLUS, MULT;

   private static final SecureRandom random = new SecureRandom();

   public static Function getFunction(GateSemantic semantic) {
      Function function = null;
      switch (semantic) {
         case PLUS:
            function = new PlusFunction();
            break;
         case MULT:
            function = new MultFunction();
            break;
      }

      return function;
   }

   public static GateSemantic getRandomGate() {
      GateSemantic semantic = random.nextBoolean() ? GateSemantic.PLUS : GateSemantic.MULT;
      return semantic;
   }
}
