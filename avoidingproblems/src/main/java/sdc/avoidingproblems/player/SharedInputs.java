package sdc.avoidingproblems.player;

import java.util.ArrayList;
import java.util.List;
import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class SharedInputs {

    private final List<SimpleRepresentation> sharedInputs;

    public SharedInputs(int NINPUTS) {
        this.sharedInputs = new ArrayList(NINPUTS);
    }

    public void add(SimpleRepresentation x) {
        sharedInputs.add(x);
    }

    public List<SimpleRepresentation> get() {
        return sharedInputs;
    }
}
