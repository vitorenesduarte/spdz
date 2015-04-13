package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class MultFunction implements Function {

   private BeaverTriple triple;

   public MultFunction() {
   }

   @Override
   public FieldElement apply(ExecutionMode mode, FieldElement... params) throws InvalidParamException, ExecutionModeNotSupportedException {
      switch (mode) {
         case LOCAL:
            if (params.length != 2) {
               throw new InvalidParamException("Invalid param number");
            } else {
               FieldElement x = params[0];
               FieldElement y = params[1];
               return x.mult(y);
            }
         case DISTRIBUTED:
            if (params.length != 3) {
               throw new InvalidParamException("Invalid param number");
            } else {
               if (triple == null) {
                  throw new InvalidParamException("Beaver triple not found");
               } else {
                  FieldElement dShare = params[0];
                  FieldElement d = params[1];
                  FieldElement e = params[2];
                  FieldElement a = triple.getA();
                  FieldElement b = triple.getB();
                  FieldElement c = triple.getC();

                  // [xy] = [c] + e[b] + d[a] + d[e]
                  // [xy] = [c] + e[b] + d[a] + [d]e
                  return dShare.mult(e).add(e.mult(a)).add(d.mult(b)).add(c);
               }
            }
         default:
            throw new ExecutionModeNotSupportedException();
      }
   }

   @Override
   public void setBeaverTriple(BeaverTriple triple) {
      this.triple = triple;
   }
}
