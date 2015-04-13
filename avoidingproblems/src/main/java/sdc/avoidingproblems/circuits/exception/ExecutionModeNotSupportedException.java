package sdc.avoidingproblems.circuits.exception;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class ExecutionModeNotSupportedException extends Exception {

   public ExecutionModeNotSupportedException() { // not supported instead of unknown
      super();
   }
   
   public ExecutionModeNotSupportedException(String message){
      super(message);
   }
   
}
