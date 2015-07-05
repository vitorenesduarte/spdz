package sdc.avoidingproblems.algebra;

import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuit.ExecutionMode;
import sdc.avoidingproblems.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlusFunction implements Function {

    @Override
    public SimpleRepresentation apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, SimpleRepresentation... params) throws InvalidParamException, ExecutionModeNotSupportedException {
        if (params.length != 2) {
            throw new InvalidParamException("Invalid param number");
        } else {
            SimpleRepresentation x = params[0];
            SimpleRepresentation y = params[1];
            return x.add(y);
        }
    }

}
