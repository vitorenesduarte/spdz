package sdc.spdz.circuits;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.spdz.circuits.exception.InvalidParamNumberException;
import sdc.spdz.circuits.exception.InvalidPlayersException;
import sdc.spdz.circuits.exception.ParamNotFoundException;
import sdc.spdz.circuits.exception.UnknownExecutionModeException;
import sdc.spdz.circuits.exception.UnknownOperationException;
import sdc.spdz.circuits.player.Player;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class MainTwo {

   private static final Logger logger = Logger.getLogger(MainTwo.class.getName());

   public static void main(String[] args) throws UnknownExecutionModeException {
      try {
         int P = 41;
         int NINPUTS = 41;
         Group group = new Group(P);
         CircuitGenerator generator = new CircuitGenerator();
         Circuit circuit = generator.generate(NINPUTS);
         Player player = new Player("",0);
         player.setCircuit(circuit);
         player.setMOD(P);

         int[] inputs = new int[NINPUTS];
         int inputSum = 0;
         for (int i = 0; i < NINPUTS; i++) {
            Random random = new Random();
            inputs[i] = random.nextInt(P);
            inputSum += inputs[i];
         }
         System.out.println("INPUT : " + inputSum);

         int[][] inputShares = new int[NINPUTS][NINPUTS];
         for (int i = 0; i < NINPUTS; i++) {
            inputShares[i] = group.createShares(inputs[i], NINPUTS);
         }

         int resultSum = 0;
         for (int i = 0; i < NINPUTS; i++) {
            player.setInputs(inputShares[i]);
            int result = player.evalCircuit(ExecutionMode.LOCAL);
            resultSum += result;
         }
         System.out.println("RESULT : " + resultSum);

      } catch (InvalidParamNumberException | UnknownOperationException | ParamNotFoundException | InvalidPlayersException ex) {
         logger.log(Level.SEVERE, null, ex);
      }
   }
}
