package sdc.avoidingproblems.circuits.gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Plus extends Gate {

   public Plus(int P) {
      super(P);
   }

   public int eval(int x, int y) {
      int result = (x + y) % getMOD();
      return result;
   }

}
