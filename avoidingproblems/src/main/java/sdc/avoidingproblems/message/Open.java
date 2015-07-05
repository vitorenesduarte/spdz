package sdc.avoidingproblems.message;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Open extends Message {

    private final BigInteger value;
    private final BigInteger MAC;

    public Open(BigInteger value, BigInteger MAC) {
        this.value = value;
        this.MAC = MAC;
    }

    public BigInteger getValue() {
        return value;
    }

    public BigInteger getMAC() {
        return MAC;
    }

    @Override
    public String toString() {
        return "Open{" + "value=" + value + ", MAC=" + MAC + '}';
    }
}