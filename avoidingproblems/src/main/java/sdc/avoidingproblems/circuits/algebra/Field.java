package sdc.avoidingproblems.circuits.algebra;

import java.security.SecureRandom;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Field { // adapt this to return other implementations of FieldElement

   private final int MOD;
   private final SecureRandom random;

   public Field(int MOD) {
      this.MOD = MOD;
      this.random = new SecureRandom();
   }

   public BigIntegerFE random() {
      int value = random.nextInt(MOD);
      return new BigIntegerFE(value, MOD);
   }

   public FieldElement[] createShares(FieldElement x, int NSHARES) {
      FieldElement[] shares = new FieldElement[NSHARES];
      shares[NSHARES - 1] = x;
      for (int i = 0; i < NSHARES - 1; i++) {
         shares[i] = random();
         shares[NSHARES - 1] = shares[NSHARES - 1].sub(shares[i]);
      }
      return shares;
   }

   public BeaverTriple randomMultiplicationTriple() {
      FieldElement a = random();
      FieldElement b = random();
      FieldElement c = new BigIntegerFE(a.intValue(), MOD).mult(b);

      BeaverTriple triple = new BeaverTriple(a, b, c);
      return triple;
   }
}
