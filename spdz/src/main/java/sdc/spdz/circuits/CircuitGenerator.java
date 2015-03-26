package sdc.spdz.circuits;

import java.security.SecureRandom;
import java.util.ArrayList;
import sdc.spdz.circuits.gate.GateType;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class CircuitGenerator {

   private final ArrayList<CircuitTriple> triples;
   private final ArrayList<Integer> notOccupied;
   private GateType gate;
   private int edgeOne;
   private int edgeTwo;
   private int edgeCount;
   private final SecureRandom random;

   public CircuitGenerator() {
      this.notOccupied = new ArrayList<>();
      this.triples = new ArrayList<>();
      this.random = new SecureRandom();
   }

   public Circuit generate(int NINPUTS) {
      edgeCount = NINPUTS;
      for (int i = 0; i < NINPUTS; i++) {
         notOccupied.add(i);
      }
      /*
       while (notOccupied.size() > 1) { // if notOccupied.size() == 1, the edge missing is the output of the circuit
       gate = GateType.getRandomGate();
       int i = random.nextInt(notOccupied.size());
       edgeOne = notOccupied.get(i);
       notOccupied.remove(i);

       i = random.nextInt(notOccupied.size());
       edgeTwo = notOccupied.get(i);
       notOccupied.remove(i);

       notOccupied.add(edgeCount++);

       CircuitTriple ct = new CircuitTriple(gate, edgeOne, edgeTwo);
       triples.add(ct);
       }
       */
      stateOne();

      CircuitTriple[] cts = new CircuitTriple[triples.size()];
      for (int i = 0; i < triples.size(); i++) {
         cts[i] = triples.get(i);
      }

      Circuit circuit = new Circuit(NINPUTS, cts);
      return circuit;
   }

   /**
    * Generates a random Gate.
    */
   private void stateOne() {
      gate = GateType.getRandomGate();
      //gate = GateType.PLUS;
      //gate = GateType.MULT;

      stateTwo();
   }

   /**
    * Decides which of the free edges will be the inputs of the random gate
    * previously generated.
    */
   private void stateTwo() {

      int i = random.nextInt(notOccupied.size());
      edgeOne = notOccupied.get(i);
      notOccupied.remove(i);

      i = random.nextInt(notOccupied.size());
      edgeTwo = notOccupied.get(i);
      notOccupied.remove(i);

      notOccupied.add(edgeCount++);

      stateThree();
   }

   /**
    * Creates a CircuitTriple with the random data previously generated. If
    * there is more than one edge free, go back to state one.
    */
   private void stateThree() {
      CircuitTriple ct = new CircuitTriple(gate, edgeOne, edgeTwo);
      triples.add(ct);

      if (notOccupied.size() > 1) { // if notOccupied.size() == 1, the edge missing is the output of the circuit
         stateOne();
      }
   }
}
