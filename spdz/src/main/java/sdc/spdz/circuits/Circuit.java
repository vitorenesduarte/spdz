package sdc.spdz.circuits;

import static sdc.spdz.circuits.gate.GateType.MULT;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Circuit {

   private final CircuitTriple[] triples;
   private final int inputSize;

   public Circuit(int inputSize, CircuitTriple... triples) {
      this.triples = triples.clone();
      this.inputSize = inputSize;
   }

   public CircuitTriple[] getTriples() {
      return triples;
   }

   public int getTriplesCount() {
      return triples.length;
   }

   public int getInputSize() {
      return inputSize;
   }

   public int getMultiplicationGatesCount() {
      int count = 0;
      for (CircuitTriple ct : triples) {
         if (ct.getGate().equals(MULT)) {
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
      for (CircuitTriple ct : triples) {
         if(ct.getGate()==MULT){
            sb.append("[").append(multiplicationCount++).append("]");
         }
         sb.append("\t").append(i++).append(" : ").append(ct.toString()).append("\n");
      }
      return sb.toString();
   }
}
