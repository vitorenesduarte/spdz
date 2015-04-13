package sdc.avoidingproblems.circuits.algebra;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BeaverTriple {

   private final ValueAndMAC a, b, c;

   public BeaverTriple(ValueAndMAC a, ValueAndMAC b, ValueAndMAC c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   public ValueAndMAC getA() {
      return a;
   }

   public ValueAndMAC getB() {
      return b;
   }

   public ValueAndMAC getC() {
      return c;
   }

}
