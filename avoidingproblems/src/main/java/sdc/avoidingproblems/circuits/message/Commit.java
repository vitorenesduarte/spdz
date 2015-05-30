package sdc.avoidingproblems.circuits.message;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Commit extends Message {

    private final Integer playerUID;
    private final Long value;

    public Commit(Integer playerUID, Long value) {
        this.playerUID = playerUID;
        this.value = value;
    }

    public Integer getPlayerUID() {
        return playerUID;
    }

    public Long getValue() {
        return value;
    }
}
