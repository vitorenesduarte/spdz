package sdc.avoidingproblems.message;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class MultiplicationShare extends Message {

    private final Long multID;
    private final BigInteger d;
    private final BigInteger e;

    public MultiplicationShare(Long multID, BigInteger d, BigInteger e) {
        this.multID = multID;
        this.d = d;
        this.e = e;
    }

    public Long getMultID() {
        return multID;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getE() {
        return e;
    }

    @Override
    public String toString() {
        return "MultiplicationShare{" + "multID=" + multID + ", d=" + d + ", e=" + e + '}';
    }

    @Override
    public MultiplicationShare clone() {
        MultiplicationShare clone = new MultiplicationShare(multID, d, e);
        return clone;
    }
}