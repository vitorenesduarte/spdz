package sdc.avoidingproblems.circuits.algebra.mac;

import sdc.avoidingproblems.circuits.algebra.FieldElement;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class SimpleRepresentation { // ValueAndMAC before

    private final FieldElement value;
    private final FieldElement MAC;

    public SimpleRepresentation(FieldElement value, FieldElement MAC) {
        this.value = value;
        this.MAC = MAC;
    }

    public FieldElement getValue() {
        return value;
    }

    public FieldElement getMAC() {
        return MAC;
    }

    public SimpleRepresentation add(SimpleRepresentation r) {
        SimpleRepresentation result = new SimpleRepresentation(this.value.add(r.getValue()), this.MAC.add(r.getMAC()));
        return result;
    }

    public SimpleRepresentation sub(SimpleRepresentation r) {
        SimpleRepresentation result = new SimpleRepresentation(this.value.sub(r.getValue()), this.MAC.sub(r.getMAC()));
        return result;
    }

    public SimpleRepresentation mult(SimpleRepresentation r) {
        SimpleRepresentation result = new SimpleRepresentation(this.value.mult(r.getValue()), this.MAC.mult(r.getMAC()));
        return result;
    }

    public SimpleRepresentation mult(FieldElement fe) {
        SimpleRepresentation result = new SimpleRepresentation(this.value.mult(fe), this.MAC.mult(fe));
        return result;
    }

    @Override
    public String toString() {
        return "SimpleRepresentation{" + "value=" + value + ", MAC=" + MAC + '}';
    }
}
