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
    public FieldElement pow(Integer power) {
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < power; i++) {
            result = result.multiply(elem).mod(MOD);
        }

        return new BigIntegerFE(result.intValue(), MOD.intValue());
    }
    
    @Override
    public int compare(FieldElement fe){
        BigInteger argument = BigInteger.valueOf(fe.intValue());
        return argument.compareTo(elem);
    }

    @Override
    public String toString() {
        return "" + intValue();
    }

}
