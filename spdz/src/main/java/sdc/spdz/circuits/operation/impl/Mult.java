package sdc.spdz.circuits.operation.impl;

import sdc.spdz.circuits.operation.InvalidParamNumberException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Mult extends OperationImpl {

   public Mult(int P) {
      super(2, P);
   }

   @Override
   public int eval(int... params) throws InvalidParamNumberException {
      int result;

      if (params.length != getArity()) {
         throw new InvalidParamNumberException();
      } else {
         result = (params[0] * params[1]) % getMod();
      }

      return result;
   }

}