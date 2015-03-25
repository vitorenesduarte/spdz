package sdc.spdz.circuits;

import java.util.ArrayList;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PreProcessedData {

   private final ArrayList<MultiplicationTriple> multiplicationTriples;

   public PreProcessedData(ArrayList<MultiplicationTriple> multiplicationTriples) {
      this.multiplicationTriples = multiplicationTriples;
   }

   public MultiplicationTriple consume() {
      MultiplicationTriple mt = null;
      if (multiplicationTriples.size() > 0) {
         mt = multiplicationTriples.remove(0);
      }

      return mt;
   }
}
