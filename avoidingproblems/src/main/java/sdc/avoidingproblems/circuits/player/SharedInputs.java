package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.circuits.algebra.mac.ValueAndMAC;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class SharedInputs {

   private final List<ValueAndMAC> sharedInputs;

   public SharedInputs(int NINPUTS) {
      this.sharedInputs = new ArrayList(NINPUTS);
   }

   public void add(ValueAndMAC x) {
      sharedInputs.add(x);
   }

   public List<ValueAndMAC> get() {
      return sharedInputs;
   }

}
