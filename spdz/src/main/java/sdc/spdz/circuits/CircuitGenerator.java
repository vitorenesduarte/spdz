package sdc.spdz.circuits;

import java.util.ArrayList;
import java.util.Random;
import sdc.spdz.circuits.operation.Gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class CircuitGenerator {

   private final ArrayList<CircuitTriple> triples;
   private final int P;
   private final ArrayList<Integer> notOccupied;
   private Gate gate;
   private int edgeOne;
   private int edgeTwo;
   private int edgeCount;

   public CircuitGenerator(int P) {
      this.P = P;
      this.notOccupied = new ArrayList<>();
      this.triples = new ArrayList<>();
   }

   public Circuit generate(int NINPUTS) {
      edgeCount = NINPUTS;
      for (int i = 0; i < NINPUTS; i++) {
         notOccupied.add(i);
      }
      stateOne();

      CircuitTriple[] cts = new CircuitTriple[triples.size()];
      for (int i = 0; i < triples.size(); i++) {
         cts[i] = triples.get(i);
      }

      Circuit circuit = new Circuit(NINPUTS, cts);
      return circuit;
   }

   private void stateOne() {
      //Random random = new Random();
      //gate = random.nextBoolean() ? Gate.PLUS : Gate.MULT;
      gate = Gate.PLUS;

      stateTwo();
   }

   private void stateTwo() {
      Random random = new Random();

      int i = random.nextInt(notOccupied.size());
      edgeOne = notOccupied.get(i);
      notOccupied.remove(i);

      i = random.nextInt(notOccupied.size());
      edgeTwo = notOccupied.get(i);
      notOccupied.remove(i);

      notOccupied.add(edgeCount++);
      
      stateThree();
   }

   private void stateThree() {
      CircuitTriple ct = new CircuitTriple(P, gate, edgeOne, edgeTwo);
      triples.add(ct);

      if (notOccupied.size() > 1) {
         stateOne();
      }
   }
}
