package sdc.avoidingproblems.circuits.algebra;

/**
 * Here, we're encapsulating how we implement field operations. It doesn't
 * matter if we're using Integer or BigInteger, as long as they implement these
 * methods.
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface FieldElement {

    Long longValue();

    FieldElement add(FieldElement fe);
    
    FieldElement add(Long value);

    FieldElement sub(FieldElement fe);

    FieldElement mult(FieldElement fe);

    FieldElement pow(Integer power);

    int compare(FieldElement fe);

    @Override
    public String toString();

}
