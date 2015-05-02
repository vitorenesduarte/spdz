package sdc.avoidingproblems.circuits;

import java.util.List;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.mac.ValueAndMAC;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Gate {

   private final GateSemantic semantic;
   private final List<Integer> inputEdges;

   public Gate(GateSemantic semantic, List<Integer> inputEdges) {
      this.semantic = semantic;
      this.inputEdges = inputEdges;
   }

   public GateSemantic getSemantic() {
      return semantic;
   }

   public List<Integer> getInputEdges() {
      return inputEdges;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      if (semantic.equals(GateSemantic.MULT)) {
         sb.append("< x , ");
      } else if (semantic.equals(GateSemantic.PLUS)) {
         sb.append("< + , ");
      }

      sb.append(inputEdges.toString()).append(">");
      return sb.toString();
   }

}
