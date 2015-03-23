package sdc.spdz.circuits;

import sdc.spdz.circuits.operation.InvalidParamNumberException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Circuit {

   private final int[] edgesValues;
   private final CircuitTriple[] triples;
   private final int inputSize;

   public Circuit(int inputSize, CircuitTriple... triples) {
      this.triples = triples.clone();
      this.inputSize = inputSize;
      this.edgesValues = new int[inputSize + triples.length];
   }

   public void init(int... inputs) throws InvalidParamNumberException {
      if (inputSize == inputs.length) {
         System.arraycopy(inputs, 0, edgesValues, 0, inputSize);
      } else {
         throw new InvalidParamNumberException();
      }
   }

   public void run() throws InvalidParamNumberException { // this is different from the exception above - how to differenciate?
      for (int i = 0; i < triples.length; i++) {
         CircuitTriple ct = triples[i];
         int[] gateInputs = ct.getGateInputs();
         int[] params = new int[gateInputs.length];
         for (int j = 0; j < gateInputs.length; j++) {
            params[j] = edgesValues[gateInputs[j]];
         }
         int result = ct.eval(params);
         edgesValues[inputSize + i] = result;
      }
   }

   public int getResult() {
      return edgesValues[edgesValues.length - 1];
   }

}
