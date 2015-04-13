package sdc.avoidingproblems.circuits.algebra;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class ValueAndMAC {

   private final FieldElement value;
   private final FieldElement MAC;

   public ValueAndMAC(FieldElement value, FieldElement MAC) {
      this.value = value;
      this.MAC = MAC;
   }

   public FieldElement getValue() {
      return value;
   }

   public FieldElement getMAC() {
      return MAC;
   }

   public ValueAndMAC add(ValueAndMAC vam) {
      ValueAndMAC result = new ValueAndMAC(this.value.add(vam.getValue()), this.MAC.add(vam.getMAC()));
      System.out.println("add: " + result.toString());
      return result;
   }

   public ValueAndMAC sub(ValueAndMAC vam) {
      ValueAndMAC result = new ValueAndMAC(this.value.sub(vam.getValue()), this.MAC.sub(vam.getMAC()));
      System.out.println("sub: " + result.toString());

      return result;
   }

   public ValueAndMAC mult(ValueAndMAC vam) {
      ValueAndMAC result = new ValueAndMAC(this.value.mult(vam.getValue()), this.MAC.mult(vam.getMAC()));
      System.out.println("mult: " + result.toString());
      return result;
   }

   public ValueAndMAC mult(FieldElement fe) {
      ValueAndMAC result = new ValueAndMAC(this.value.mult(fe), this.MAC.mult(fe));
      System.out.println("multC: " + result.toString());
      return result;
   }

   @Override
   public String toString() {
      return "{" + value + "," + MAC + "}";
   }
}
