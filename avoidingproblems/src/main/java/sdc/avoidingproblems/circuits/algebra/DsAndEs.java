package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class DsAndEs {

    private FieldElement d, e;
    private final Long MOD;
    private int numberOfShares;

    public DsAndEs(Long d, Long e, Long MOD, Class<?> clazz) throws ClassNotSupportedException {
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

    public void addToD(Long x) {
        d = d.add(new BigIntegerFE(x, MOD));
    }

    public void addToE(Long x) {
        e = e.add(new BigIntegerFE(x, MOD));
    }

    public void incrNumberOfShares() {
        numberOfShares++;
    }

    @Override
    public String toString() {
        return "{" + d + "," + e + "}";
    }

}
