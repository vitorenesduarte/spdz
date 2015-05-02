package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.algebra.mac.ValueAndMAC;
import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface Function {

   ValueAndMAC apply(ExecutionMode mode, BeaverTriple triple, FieldElement d, FieldElement e, ValueAndMAC... params) throws InvalidParamException, ExecutionModeNotSupportedException;

}
