package sdc.avoidingproblems.circuits.player;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Share {

   private int d, e;
   private int numberOfShares;

   public Share(int d, int e) {
      this.d = d;
      this.e = e;
      this.numberOfShares = 1;
   }

   public int getD() {
      return d;
   }

   public int getE() {
      return e;
   }

   public int getNumberOfShares() {
      return numberOfShares;
   }

   public void addToD(int x) {
      d += x;
   }

   public void addToE(int x) {
      e += x;
   }

   @Override
   public String toString() {
      return "{" + d + "," + e + "}";
   }

   public void incrNumberOfShares() {
      numberOfShares++;
   }
}
