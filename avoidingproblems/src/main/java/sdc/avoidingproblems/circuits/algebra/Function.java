package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface Function {

   SimpleRepresentation apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, SimpleRepresentation... params) throws InvalidParamException, ExecutionModeNotSupportedException;

}
