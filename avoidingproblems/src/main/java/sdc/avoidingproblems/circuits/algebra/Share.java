package sdc.avoidingproblems.circuits.algebra;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Share {

   private FieldElement d, e;
   private final int MOD;
   private int numberOfShares;

   public Share(int d, int e, int MOD) {
      this.MOD = MOD;
      this.d = new BigIntegerFE(d, MOD);
      this.e = new BigIntegerFE(e, MOD);
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
