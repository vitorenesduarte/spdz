package sdc.avoidingproblems.circuits.algebra;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BigIntegerFE implements FieldElement {

    private final BigInteger elem;
    private final BigInteger MOD;

    public BigIntegerFE(BigInteger value, BigInteger MOD) {
        this.elem = value;
        this.MOD = MOD;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return elem;
    }

    @Override
    public FieldElement add(FieldElement fe) {
        BigInteger result = this.elem.add(fe.bigIntegerValue());
        result = result.mod(MOD);
        return new BigIntegerFE(result, MOD);
    }
    
    @Override
    public FieldElement add(BigInteger value){
        BigInteger result = this.elem.add(value);
        result = result.mod(MOD);
        return new BigIntegerFE(result, MOD);
    }

    @Override
    public FieldElement sub(FieldElement fe) {
        BigInteger result = this.elem.subtract(fe.bigIntegerValue());
        result = result.mod(MOD);
        if(result.compareTo(BigInteger.ZERO) < 0){
            result.add(MOD);
        }
        return new BigIntegerFE(result, MOD);
    }

    @Override
    public FieldElement mult(FieldElement fe) {
        BigInteger result = this.elem.multiply(fe.bigIntegerValue());
        result = result.mod(MOD);
        return new BigIntegerFE(result, MOD);
    }

    @Override
    public FieldElement pow(Integer power) {
        BigInteger result = this.elem.modPow(BigInteger.valueOf(power), MOD);
        return new BigIntegerFE(result, MOD);
    }

    @Override
    public String toString() {
        return elem.toString();
    }

}
