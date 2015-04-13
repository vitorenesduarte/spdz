package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class DsAndEs {

   private FieldElement d, e;
   private final int MOD;
   private int numberOfShares;

   public DsAndEs(int d, int e, int MOD, Class<?> clazz) throws ClassNotSupportedException {
      this.MOD = MOD;
      this.d = Util.getFieldElementInstance(clazz, d, MOD);
      this.e = Util.getFieldElementInstance(clazz, e, MOD);
      this.numberOfShares = 1;
   }

   public FieldElement getD() {
      return d;
   }

   public FieldElement getE() {
      return e;
   }

   public int getNumberOfShares() {
      return numberOfShares;
   }

   public void addToD(int x) {
      d = d.add(new BigIntegerFE(x, MOD));
   }

   public void addToE(int x) {
      e = e.add(new BigIntegerFE(x, MOD));
   }

   @Override
   public String toString() {
      return "{" + d + "," + e + "}";
   }

   public void incrNumberOfShares() {
      numberOfShares++;
   }
}
