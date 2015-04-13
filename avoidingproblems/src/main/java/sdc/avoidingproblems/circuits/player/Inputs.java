package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.circuits.algebra.FieldElement;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Inputs {

   private final List<FieldElement> inputs;

   public Inputs(int NINPUTS) {
      this.inputs = new ArrayList(NINPUTS);
   }

   public void add(FieldElement x) {
      inputs.add(x);
   }

   public List<FieldElement> get() {
      return inputs;
   }

}
