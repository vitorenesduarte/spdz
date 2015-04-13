package sdc.avoidingproblems.circuits.algebra;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BeaverTriple {
   private final FieldElement a, b, c;

   public BeaverTriple(FieldElement a, FieldElement b, FieldElement c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   public FieldElement getA() {
      return a;
   }

   public FieldElement getB() {
      return b;
   }

   public FieldElement getC() {
      return c;
   }
   
}
