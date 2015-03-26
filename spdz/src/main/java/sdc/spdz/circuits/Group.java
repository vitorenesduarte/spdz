package sdc.spdz.circuits;

import java.security.SecureRandom;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Group {

   private final int MOD;
   private final SecureRandom random;

   public Group(int MOD) {
      this.MOD = MOD;
      this.random = new SecureRandom();
   }

   public int random() {
      return random.nextInt(MOD);
   }

   public int[] createShares(int x, int NSHARES) {
      int[] shares = new int[NSHARES];
      shares[NSHARES - 1] = x;
      for (int i = 0; i < NSHARES - 1; i++) {
         shares[i] = random();
         shares[NSHARES - 1] -= shares[i];
      }

      shares[NSHARES - 1] %= MOD;
      if (shares[NSHARES - 1] < 0) {
         shares[NSHARES - 1] += MOD;
      }

      return shares;
   }

   public MultiplicationTriple randomMultiplicationTriple() {
      int a = random();
      int b = random();
      int c = (a * b) % MOD;

      MultiplicationTriple mt = new MultiplicationTriple(a, b, c);
      return mt;
   }
}
