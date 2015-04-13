package sdc.avoidingproblems.circuits.algebra;

import sdc.avoidingproblems.circuits.ExecutionMode;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public interface Function {

   FieldElement apply(ExecutionMode mode, FieldElement... params) throws InvalidParamException, ExecutionModeNotSupportedException;

   void setBeaverTriple(BeaverTriple triple);
}
