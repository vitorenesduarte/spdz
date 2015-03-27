package sdc.spdz.circuits.player;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Inputs {

   private final int[] inputs;
   private int current;

   public Inputs(int NINPUTS) {
      this.inputs = new int[NINPUTS];
      this.current = 0;
   }

   public void add(int x) {
      inputs[current++] = x;
   }

   public int[] get() {
      return inputs;
   }

}
