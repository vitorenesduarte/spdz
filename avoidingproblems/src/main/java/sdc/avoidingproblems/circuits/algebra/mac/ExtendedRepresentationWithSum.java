package sdc.avoidingproblems.circuits.algebra.mac;

import java.util.Map;
import sdc.avoidingproblems.circuits.algebra.FieldElement;

/**
 *
 * @author Vitor Enes
 */
public class ExtendedRepresentationWithSum extends ExtendedRepresentation {

    private final FieldElement sum;

    public ExtendedRepresentationWithSum(FieldElement beta, FieldElement value, FieldElement sum, Map<String, FieldElement> playersMACShares) {
        super(beta, value, playersMACShares);
        this.sum = sum;
    }

    public FieldElement getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "ExtendedRepresentation{" + "beta=" + getBeta() + ", value=" + getValue() + ", sum=" + sum + ", playersMACShares=" + getMACShares() + '}';
    }
}
