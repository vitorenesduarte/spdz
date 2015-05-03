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
    private final Map<Integer, FieldElement> playersMACShares;

    public ExtendedRepresentation(FieldElement beta, FieldElement value, Map<Integer, FieldElement> playersMACShares) {
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

    public FieldElement getMAC(Integer playerID) {
        return playersMACShares.get(playerID);
    }
}
