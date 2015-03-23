package sdc.spdz.circuits.operation;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public interface Operation {

   int eval(int... params) throws InvalidParamNumberException;
}
