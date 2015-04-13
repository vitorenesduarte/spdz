package sdc.avoidingproblems.circuits;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class CircuitGenerator {
   
   private final List<Gate> gates;
   private final List<Integer> notOccupied;
   private GateSemantic semantic;
   private int edgeOne;
   private int edgeTwo;
   private int edgeCount;
   private final SecureRandom random;

   public CircuitGenerator() {
      this.notOccupied = new ArrayList<>();
      this.gates = new ArrayList<>();
      this.random = new SecureRandom();
   }

   public Circuit generate(int NINPUTS) {
      edgeCount = NINPUTS;
      for (int i = 0; i < NINPUTS; i++) {
         notOccupied.add(i);
      }
  
      while (notOccupied.size() > 1) { // if notOccupied.size() == 1, the edge missing is the output of the circuit
         semantic = GateSemantic.getRandomGate();
         int i = random.nextInt(notOccupied.size());
         edgeOne = notOccupied.get(i);
         notOccupied.remove(i);

         i = random.nextInt(notOccupied.size());
         edgeTwo = notOccupied.get(i);
         notOccupied.remove(i);

         notOccupied.add(edgeCount++);

         List<Integer> inputEdges = new ArrayList(2);
         inputEdges.add(edgeOne);
         inputEdges.add(edgeTwo);
         Gate gate = new Gate(semantic, inputEdges);
         gates.add(gate);
      }


      Circuit circuit = new Circuit(NINPUTS, gates);
      return circuit;
   }
}
