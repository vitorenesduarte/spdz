package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlusFunction implements Function {

   public PlusFunction() {
   }

   @Override
   public FieldElement apply(ExecutionMode mode, FieldElement... params) throws InvalidParamException {
      if (params.length != 2) {
         throw new InvalidParamException("Invalid param number");
      } else {
         FieldElement x = params[0];
         FieldElement y = params[1];
         FieldElement result = x.add(y);
         return x.add(y);
      }
   }

   @Override
   public void setBeaverTriple(BeaverTriple triple) {
      throw new UnsupportedOperationException("Plus function does not need beaver multiplication triples"); //To change body of generated methods, choose Tools | Templates.
   }
}
