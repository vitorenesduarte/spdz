package sdc.spdz.algebra;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class FieldElement {

    private final BigInteger elem;
    private final BigInteger MOD;

    public FieldElement(BigInteger value, BigInteger MOD) {
        this.elem = value;
        this.MOD = MOD;
    }

    public BigInteger bigIntegerValue() {
        return elem;
    }

    public FieldElement add(FieldElement fe) {
        BigInteger result = this.elem.add(fe.bigIntegerValue());
        result = result.mod(MOD);
        return new FieldElement(result, MOD);
    }
    
    public FieldElement add(BigInteger value){
        BigInteger result = this.elem.add(value);
        result = result.mod(MOD);
        return new FieldElement(result, MOD);
    }

    public FieldElement sub(FieldElement fe) {
        BigInteger result = this.elem.subtract(fe.bigIntegerValue());
        result = result.mod(MOD);
        if(result.compareTo(BigInteger.ZERO) < 0){
            result.add(MOD);
        }
        return new FieldElement(result, MOD);
    }

    public FieldElement mult(FieldElement fe) {
        BigInteger result = this.elem.multiply(fe.bigIntegerValue());
        result = result.mod(MOD);
        return new FieldElement(result, MOD);
    }

    public FieldElement pow(Integer power) {
        BigInteger result = this.elem.modPow(BigInteger.valueOf(power), MOD);
        return new FieldElement(result, MOD);
    }

    @Override
    public String toString() {
        return elem.toString();
    }

}
