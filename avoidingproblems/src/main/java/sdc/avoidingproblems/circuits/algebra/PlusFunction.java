package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.algebra.mac.ValueAndMAC;
import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlusFunction implements Function {

   public PlusFunction() {
   }

   @Override
   public ValueAndMAC apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, ValueAndMAC... params) throws InvalidParamException, ExecutionModeNotSupportedException {
      if (params.length != 2) {
         throw new InvalidParamException("Invalid param number");
      } else {
         ValueAndMAC x = params[0];
         ValueAndMAC y = params[1];
         return x.add(y);
      }
   }

}
