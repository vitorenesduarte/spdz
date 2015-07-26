package sdc.avoidingproblems.message;

import java.math.BigInteger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Commit extends Message {

    private final String player; // host:port
    private final BigInteger value;

    public Commit(String player, BigInteger value) {
        this.player = player;
        this.value = value;
    }

    public String getPlayer() {
        return player;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Commit{" + "player=" + player + ", value=" + value + '}';
    }
}