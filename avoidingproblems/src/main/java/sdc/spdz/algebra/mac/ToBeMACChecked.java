package sdc.avoidingproblems.algebra.mac;

import sdc.avoidingproblems.algebra.FieldElement;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class ToBeMACChecked {

    private final SimpleRepresentation share;
    private final FieldElement openedValue;

    public ToBeMACChecked(SimpleRepresentation share, FieldElement openedValue) {
        this.share = share;
        this.openedValue = openedValue;
    }

    public SimpleRepresentation getShare() {
        return share;
    }

    public FieldElement getOpenedValue() {
        return openedValue;
    }

    @Override
    public String toString() {
        return "ToBeMACChecked{" + "share=" + share + ", openedValue=" + openedValue + '}';
    }
}
