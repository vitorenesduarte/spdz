package sdc.avoidingproblems.circuits.algebra;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BigIntegerFE implements FieldElement {

   private final BigInteger elem;
   private final BigInteger MOD;

   public BigIntegerFE(Integer value, Integer MOD) {
      this.elem = BigInteger.valueOf(value);
      this.MOD = BigInteger.valueOf(MOD);
   }

   @Override
   public int intValue() {
      return elem.intValue();
   }

   @Override
   public FieldElement add(FieldElement fe) {
      BigInteger result = this.elem.add(BigInteger.valueOf(fe.intValue()));
      result = result.mod(MOD);
      return new BigIntegerFE(result.intValue(), MOD.intValue());
   }

   @Override
   public FieldElement sub(FieldElement fe) {
      BigInteger result = this.elem.subtract(BigInteger.valueOf(fe.intValue()));
      result = result.mod(MOD);
      return new BigIntegerFE(result.intValue(), MOD.intValue());
   }

   @Override
   public FieldElement mult(FieldElement fe) {
      BigInteger result = this.elem.multiply(BigInteger.valueOf(fe.intValue()));
      result = result.mod(MOD);
      return new BigIntegerFE(result.intValue(), MOD.intValue());
   }

   @Override
   public FieldElement getInstance(int value, int MOD) {
      BigIntegerFE instance = new BigIntegerFE(value, MOD);
      return instance;
   }

   @Override
   public String toString() {
      return "" + intValue();
   }

}
