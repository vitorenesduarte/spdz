package sdc.avoidingproblems.algebra;

import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuit.ExecutionMode;
import sdc.avoidingproblems.exception.InvalidParamException;
import sdc.avoidingproblems.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface Function {

    SimpleRepresentation apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, SimpleRepresentation... params) throws InvalidParamException, ExecutionModeNotSupportedException;

}
