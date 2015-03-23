package sdc.spdz.circuits.operation.impl;

import sdc.spdz.circuits.operation.Operation;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public abstract class OperationImpl implements Operation {

   private final int arity;
   private final int mod;

   public OperationImpl(int arity, int mod) {
      this.arity = arity;
      this.mod = mod;
   }

   public int getArity() {
      return arity;
   }

   public int getMod() {
      return mod;
   }

}
