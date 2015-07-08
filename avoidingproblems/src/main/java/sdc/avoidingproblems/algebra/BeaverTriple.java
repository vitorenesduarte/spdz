package sdc.avoidingproblems.algebra;

import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class BeaverTriple {

    private final SimpleRepresentation a, b, c;

    public BeaverTriple(SimpleRepresentation a, SimpleRepresentation b, SimpleRepresentation c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleRepresentation getA() {
        return a;
    }

    public SimpleRepresentation getB() {
        return b;
    }

    public SimpleRepresentation getC() {
        return c;
    }
}
