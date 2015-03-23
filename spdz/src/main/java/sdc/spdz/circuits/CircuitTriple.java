package sdc.spdz.circuits;

import sdc.spdz.circuits.operation.Gate;
import sdc.spdz.circuits.operation.InvalidParamNumberException;
import sdc.spdz.circuits.operation.Operation;
import sdc.spdz.circuits.operation.impl.Mult;
import sdc.spdz.circuits.operation.impl.Plus;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class CircuitTriple {

   private final Gate gate;
   private final int[] gateInputs;
   private final int P;

   public CircuitTriple(int P, Gate op, int... gateInputs) {
      this.gate = op;
      this.P = P;
      this.gateInputs = gateInputs.clone();
   }

   public int[] getGateInputs() {
      return gateInputs;
   }

   public int eval(int[] params) throws InvalidParamNumberException {
      Operation op = null;

      switch (gate) {
         case PLUS:
            op = new Plus(P);
            break;
         case MULT:
            op = new Mult(P);
            break;
      }

      // it will never be null
      return op.eval(params);
   }
}
