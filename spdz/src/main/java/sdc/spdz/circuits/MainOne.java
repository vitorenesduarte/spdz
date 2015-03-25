package sdc.spdz.circuits;

import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.spdz.circuits.operation.Gate;
import sdc.spdz.circuits.operation.InvalidParamNumberException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class MainOne {

   private static final Logger logger = Logger.getLogger(MainOne.class.getName());
   
   public static void main(String[] args) {
      try {
         int P = 97;
         
         CircuitTriple ct1 = new CircuitTriple(P, Gate.PLUS, 0, 1);
         CircuitTriple ct2 = new CircuitTriple(P, Gate.MULT, 2, 3);
         Circuit circuit = new Circuit(3, ct1, ct2);
         circuit.init(2, 1, 8);
         circuit.run();
         System.out.println(circuit.getResult());
      } catch (InvalidParamNumberException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

   }
}
