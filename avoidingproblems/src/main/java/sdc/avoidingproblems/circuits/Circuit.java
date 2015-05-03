package sdc.avoidingproblems.circuits;

import java.util.ArrayList;
import java.util.List;
import static sdc.avoidingproblems.circuits.ExecutionMode.LOCAL;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

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

   public FieldElement eval(List<FieldElement> inputs) throws InvalidParamException, ExecutionModeNotSupportedException {
      List<SimpleRepresentation> values = new ArrayList();
      for (FieldElement fe : inputs) {
         values.add(new SimpleRepresentation(fe, fe)); // fake fake fake
      }
      for (Gate gate : gates) {
         List<Integer> inputEdges = gate.getInputEdges();
         SimpleRepresentation[] params = new SimpleRepresentation[inputEdges.size()];
         for (int j = 0; j < inputEdges.size(); j++) {
            params[j] = values.get(inputEdges.get(j));
         }

         values.add(GateSemantic.getFunction(gate.getSemantic()).apply(LOCAL, null, null, null, params));
      }

      return values.get(values.size() - 1).getValue();
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
