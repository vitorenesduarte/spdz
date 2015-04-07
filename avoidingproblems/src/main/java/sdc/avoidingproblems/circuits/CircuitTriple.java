package sdc.avoidingproblems.circuits;

import java.util.Arrays;
import sdc.avoidingproblems.circuits.gate.GateType;
import static sdc.avoidingproblems.circuits.gate.GateType.MULT;
import static sdc.avoidingproblems.circuits.gate.GateType.PLUS;

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

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      if (gate.equals(MULT)) {
         sb.append("< x , ");
      } else if (gate.equals(PLUS)) {
         sb.append("< + , ");
      }
      
      sb.append(Arrays.toString(gateInputs)).append(">");
      return sb.toString();
   }
}
