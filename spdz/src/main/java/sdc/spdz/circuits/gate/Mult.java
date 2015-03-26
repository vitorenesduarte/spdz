package sdc.spdz.circuits.gate;

import sdc.spdz.circuits.MultiplicationTriple;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Mult extends Gate {

   public Mult(int MOD) {
      super(MOD);
   }

   public int eval(int x, int y) {
      int result = (x * y) % getMOD();
      return result;
   }

   public int evalDistributed(int dShared, int d, int e, MultiplicationTriple mt) {
      int result = dShared * e + e * mt.getA() + d * mt.getB() + mt.getC();
      result %= getMOD();
      return result;
      // [xy] = [c] + e[b] + d[a] + d[e]
      // [xy] = [c] + e[b] + d[a] + [d]e
   }

}
