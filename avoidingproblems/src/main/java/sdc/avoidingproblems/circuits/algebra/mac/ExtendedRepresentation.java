package sdc.avoidingproblems.circuits.algebra.mac;

import java.util.Map;
import sdc.avoidingproblems.circuits.algebra.FieldElement;

/**
 *
 * @author Vitor Enes
 */
public class ExtendedRepresentation {

    private final FieldElement beta;
    private final FieldElement value;
    private final Map<String, FieldElement> playersMACShares; // localhost:4567 -> MAC

    public ExtendedRepresentation(FieldElement beta, FieldElement value, Map<String, FieldElement> playersMACShares) {
        this.beta = beta;
        this.value = value;
        this.playersMACShares = playersMACShares;
    }

    public FieldElement getValue() {
        return value;
    }

    public FieldElement getBeta() {
        return beta;
    }

    public FieldElement getMAC(String playerID) {
        return playersMACShares.get(playerID);
    }
    
    public Map<String, FieldElement> getMACShares(){
        return playersMACShares;
    }

    @Override
    public String toString() {
        return "ExtendedRepresentation{" + "beta=" + beta + ", value=" + value + ", playersMACShares=" + playersMACShares + '}';
    }
}
