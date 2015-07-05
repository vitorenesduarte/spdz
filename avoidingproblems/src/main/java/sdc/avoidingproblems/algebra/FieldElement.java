package sdc.avoidingproblems.algebra;

import java.math.BigInteger;

/**
 * Here, we're encapsulating how we implement field operations. It doesn't
 * matter if we're using Integer or BigInteger, as long as they implement these
 * methods.
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface FieldElement {

    BigInteger bigIntegerValue();

    FieldElement add(FieldElement fe);
    
    FieldElement add(BigInteger value);

    FieldElement sub(FieldElement fe);

    FieldElement mult(FieldElement fe);

    FieldElement pow(Integer power);

    @Override
    public String toString();
    
}
