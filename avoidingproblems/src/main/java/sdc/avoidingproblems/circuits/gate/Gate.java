package sdc.avoidingproblems.circuits.gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public abstract class Gate {

   private final int MOD;

   public Gate(int MOD) {
      this.MOD = MOD;
   }

   public int getMOD() {
      return MOD;
   }
}
