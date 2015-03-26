package sdc.spdz.circuits;

import java.util.ArrayList;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PreProcessedData {

   private final ArrayList<MultiplicationTriple> multiplicationTriples;

   public PreProcessedData() {
      this.multiplicationTriples = new ArrayList<>();
   }

   public PreProcessedData(ArrayList<MultiplicationTriple> multiplicationTriples) {
      this.multiplicationTriples = multiplicationTriples;
   }
   
   public void add(MultiplicationTriple triple){
      this.multiplicationTriples.add(triple);
   }

   public MultiplicationTriple consume() {
      MultiplicationTriple mt = null;
      if (multiplicationTriples.size() > 0) {
         mt = multiplicationTriples.remove(0);
      }

      return mt;
   }
}
