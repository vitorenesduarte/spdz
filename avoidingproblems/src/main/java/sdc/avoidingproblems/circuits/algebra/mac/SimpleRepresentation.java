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
        SimpleRepresentation result = new SimpleRepresentation(value.add(r.getValue()), MAC.add(r.getMAC()));
        return result;
    }

    public SimpleRepresentation sub(SimpleRepresentation r) {
        SimpleRepresentation result = new SimpleRepresentation(value.sub(r.getValue()), MAC.sub(r.getMAC()));
        return result;
    }

    public SimpleRepresentation mult(SimpleRepresentation r) {
        SimpleRepresentation result = new SimpleRepresentation(value.mult(r.getValue()), MAC.mult(r.getMAC()));
        return result;
    }

    public SimpleRepresentation mult(FieldElement fe) {
        SimpleRepresentation result = new SimpleRepresentation(value.mult(fe), MAC.mult(fe));
        return result;
    }

    public SimpleRepresentation pow(Integer power) {
        SimpleRepresentation result = new SimpleRepresentation(value.pow(power), MAC.pow(power));
        return result;
    }

    @Override
    public String toString() {
        return "SimpleRepresentation{" + "value=" + value + ", MAC=" + MAC + '}';
    }
}
