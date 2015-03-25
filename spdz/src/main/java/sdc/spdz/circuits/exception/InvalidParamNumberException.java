package sdc.spdz.circuits.exception;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class InvalidParamNumberException extends Exception {

   public InvalidParamNumberException(){
      super();
   }
   
   public InvalidParamNumberException(String s) {
      super(s);
   }
}
