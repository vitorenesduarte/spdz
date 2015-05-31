package sdc.avoidingproblems.circuits.message;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class OpenCommited extends Message {

    private final String player; // host:port
    private final Long value;
    private final Long MAC;

    public OpenCommited(String player, Long value, Long MAC) {
        this.player = player;
        this.value = value;
        this.MAC = MAC;
    }

    public String getPlayer() {
        return player;
    }

    public Long getValue() {
        return value;
    }

    public Long getMAC() {
        return MAC;
    }

    @Override
    public String toString() {
        return "OpenCommited{" + "player=" + player + ", value=" + value + ", MAC=" + MAC + '}';
    }
}
