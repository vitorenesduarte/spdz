package sdc.spdz.circuits;

import sdc.spdz.circuits.exception.InvalidParamNumberException;
import sdc.spdz.circuits.exception.UnknownOperationException;

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
}
