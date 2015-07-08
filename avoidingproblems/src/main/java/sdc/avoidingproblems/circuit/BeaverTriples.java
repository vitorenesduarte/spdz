package sdc.avoidingproblems.circuit;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.algebra.BeaverTriple;

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
        return multiplicationTriples.size() > 0 ? multiplicationTriples.remove(0) : null;
    }
}
