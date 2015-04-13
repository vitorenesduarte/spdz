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
   public ValueAndMAC apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, ValueAndMAC... params) throws InvalidParamException, ExecutionModeNotSupportedException {
      switch (mode) {
         case LOCAL:
            if (params.length != 2) {
               throw new InvalidParamException("Invalid param number");
            } else {
               ValueAndMAC x = params[0];
               ValueAndMAC y = params[1];
               return x.mult(y);
            }
         case DISTRIBUTED:
            if (params.length != 1) {
               throw new InvalidParamException("Invalid param number");
            } else {
               ValueAndMAC dShare = params[0];
               ValueAndMAC a = triple.getA();
               ValueAndMAC b = triple.getB();
               ValueAndMAC c = triple.getC();

                  // [xy] = [c] + e[b] + d[a] + d[e]
               // [xy] = [c] + e[b] + d[a] + [d]e
               return dShare.mult(e).add(a.mult(e)).add(b.mult(d)).add(c);
            }
         default:
            throw new ExecutionModeNotSupportedException();
      }
   }

}
