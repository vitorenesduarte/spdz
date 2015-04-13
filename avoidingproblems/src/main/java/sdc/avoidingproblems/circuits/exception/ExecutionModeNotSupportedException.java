package sdc.avoidingproblems.circuits.exception;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class ExecutionModeNotSupportedException extends Exception {

   public ExecutionModeNotSupportedException() {
      super();
   }
   
   public ExecutionModeNotSupportedException(String message){
      super(message);
   }
   
}
