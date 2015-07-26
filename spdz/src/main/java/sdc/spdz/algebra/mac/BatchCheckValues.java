package sdc.spdz.algebra.mac;

import java.util.Map;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BatchCheckValues {

    private final ExtendedRepresentation u;
    private final ExtendedRepresentationWithSum myCommit;
    private final Map<String, ExtendedRepresentation> theirCommit;

    public BatchCheckValues(ExtendedRepresentation u, ExtendedRepresentationWithSum myCommit, Map<String, ExtendedRepresentation> theirCommit) {
        this.u = u;
        this.myCommit = myCommit;
        this.theirCommit = theirCommit;
    }

    public ExtendedRepresentation getU() {
        return u;
    }

    public ExtendedRepresentationWithSum getMyCommit() {
        return myCommit;
    }

    public Map<String, ExtendedRepresentation> getTheirCommit() {
        return theirCommit;
    }

    @Override
    public String toString() {
        return "BatchCheckValues{" + "u=" + u + ", myCommit=" + myCommit + ", theirCommit=" + theirCommit + '}';
    }
}
