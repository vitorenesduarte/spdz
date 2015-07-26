package sdc.spdz.algebra;

import sdc.spdz.algebra.mac.SimpleRepresentation;
import sdc.spdz.circuit.ExecutionMode;
import sdc.spdz.exception.InvalidParamException;
import sdc.spdz.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface Function {

    SimpleRepresentation apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, SimpleRepresentation... params) throws InvalidParamException, ExecutionModeNotSupportedException;

}
