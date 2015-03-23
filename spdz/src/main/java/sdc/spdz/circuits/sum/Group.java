package sdc.spdz.circuits.sum;

import java.util.Random;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Group {

   private final int P;

   public Group(int P) {
      this.P = P;
   }

   public int random() {
      Random r = new Random();
      return r.nextInt(P);
   }

   public int[] createShares(int x, int NSHARES) {
      int[] shares = new int[NSHARES];
      shares[NSHARES - 1] = x;
      for (int i = 0; i < NSHARES - 1; i++) {
         shares[i] = random();
         shares[NSHARES - 1] -= shares[i];
      }

      shares[NSHARES - 1] %= P;
      if (shares[NSHARES - 1] < P) {
         shares[NSHARES - 1] += P;
      }

      return shares;
   }
}
