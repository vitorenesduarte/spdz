package sdc.avoidingproblems.circuits.message;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class OpenCommited extends Message {

    private final String player; // host:port
    private final BigInteger value;
    private final BigInteger MAC;

    public OpenCommited(String player, BigInteger value, BigInteger MAC) {
        this.player = player;
        this.value = value;
        this.MAC = MAC;
    }

    public String getPlayer() {
        return player;
    }

    public BigInteger getValue() {
        return value;
    }

    public BigInteger getMAC() {
        return MAC;
    }

    @Override
    public String toString() {
        return "OpenCommited{" + "player=" + player + ", value=" + value + ", MAC=" + MAC + '}';
    }
}