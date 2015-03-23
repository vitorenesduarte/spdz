package sdc.spdz.circuits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import sdc.spdz.circuits.operation.Gate;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class CircuitGenerator {

   private final ArrayList<CircuitTriple> triples;
   private int n_fios_entrada;
   private final int P;
   private int[] fios_entrada;
   private final ArrayList<Boolean> ocupacao;
   private Gate gate;
   private int aresta2;
   private int aresta1;

   public CircuitGenerator(int P) {
      this.P = P;
      this.ocupacao = new ArrayList<>();
      this.triples = new ArrayList<>();
   }

   public Circuit generate(int n_fios_entrada) {
      this.n_fios_entrada = n_fios_entrada;
      this.fios_entrada = new int[n_fios_entrada];
      stateOne();

      CircuitTriple[] cts = new CircuitTriple[triples.size()];
      for (int i = 0; i < triples.size(); i++) {
         cts[i] = triples.get(i);
      }
      Circuit circuit = new Circuit(n_fios_entrada, cts);
      return circuit;
   }

   private void stateOne() {
      Random random = new Random();

      for (int i = 0; i < n_fios_entrada; i++) {
         fios_entrada[i] = random.nextInt(P);
      }

      stateTwo();
   }

   private void stateTwo() {
      for (int i = 0; i < n_fios_entrada; i++) {
         ocupacao.add(false);
      }

      stateThree();
   }

   private void stateThree() {
      Random random = new Random();
      //gate = random.nextBoolean() ? Gate.PLUS : Gate.MULT;
      gate = Gate.PLUS;
      //System.out.println(Arrays.toString(ocupacao.toArray()));

      stateFour();
   }

   private void stateFour() {
      ArrayList<Integer> livres = new ArrayList();
      for (int i = 0; i < ocupacao.size(); i++) {
         if (!ocupacao.get(i)) {
            livres.add(i);
         }
      }

      Random random = new Random();

      int i = random.nextInt(livres.size());
      aresta1 = livres.get(i);
      livres.remove(i);

      i = random.nextInt(livres.size());
      aresta2 = livres.get(i);
      livres.remove(i);

      stateFive();
   }

   private void stateFive() {
      ocupacao.set(aresta1, true);
      ocupacao.set(aresta2, true);

      stateSix();
   }

   private void stateSix() {
      ocupacao.add(false);

      stateSeven();
   }

   private void stateSeven() {
      CircuitTriple ct = new CircuitTriple(P, gate, aresta1, aresta2);
      triples.add(ct);

      if (!completo()) {
         stateThree();
      }
   }

   private boolean completo() {
      int count = 0;
      for (Boolean b : ocupacao) {
         if (!b) {
            count++;
         }
      }

      return count == 1;
   }

}
