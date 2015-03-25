package sdc.spdz.circuits;

import sdc.spdz.circuits.exception.UnknownOperationException;
import sdc.spdz.circuits.gate.GateType;
import sdc.spdz.circuits.gate.Mult;
import sdc.spdz.circuits.gate.Plus;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class CircuitTriple {

   private final GateType gate;
   private final int[] gateInputs;

   public CircuitTriple(GateType gate, int... gateInputs) {
      this.gate = gate;
      this.gateInputs = gateInputs.clone();
   }

   public GateType getGate() {
      return gate;
   }

   public int[] getGateInputs() {
      return gateInputs;
   }
}
