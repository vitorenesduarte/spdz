package sdc.avoidingproblems.circuits;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BeaverTriples {

    private final List<BeaverTriple> multiplicationTriples;

    public BeaverTriples() {
        this.multiplicationTriples = new ArrayList<>();
    }

    public BeaverTriples(List<BeaverTriple> multiplicationTriples) {
        this.multiplicationTriples = multiplicationTriples;
    }

    public void add(BeaverTriple triple) {
        this.multiplicationTriples.add(triple);
    }

    public BeaverTriple consume() {
        BeaverTriple triple = null;
        if (multiplicationTriples.size() > 0) {
            triple = multiplicationTriples.remove(0);
        }

        return triple;
    }
}
