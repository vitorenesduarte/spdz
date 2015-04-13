package sdc.avoidingproblems.circuits;

import java.util.List;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Circuit {

   private final int inputSize;
   private final List<Gate> gates;

   public Circuit(int inputSize, List<Gate> gates) {
      this.gates = gates;
      this.inputSize = inputSize;
   }

   public int getInputSize() {
      return inputSize;
   }

   public List<Gate> getGates() {
      return gates;
   }

   public int getGateCount() {
      return gates.size();
   }

   public int getMultiplicationGatesCount() {
      int count = 0;
      for (Gate gate : gates) {
         if (gate.getSemantic().equals(GateSemantic.MULT)) {
            count++;
         }
      }

      return count;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      int i = inputSize;
      int multiplicationCount = 0;
      for (Gate gate : gates) {
         if (gate.getSemantic().equals(GateSemantic.MULT)) {
            sb.append("[").append(multiplicationCount++).append("]");
         }
         sb.append("\t").append(i++).append(" : ").append(gate.toString()).append("\n");
      }
      return sb.toString();
   }
}
