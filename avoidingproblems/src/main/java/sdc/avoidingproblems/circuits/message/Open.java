package sdc.avoidingproblems.circuits.message;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Open extends Message {

    private final Long value;
    private final Long MAC;

    public Open(Long value, Long MAC) {
        this.value = value;
        this.MAC = MAC;
    }

    public Long getValue() {
        return value;
    }

    public Long getMAC() {
        return MAC;
    }
}
