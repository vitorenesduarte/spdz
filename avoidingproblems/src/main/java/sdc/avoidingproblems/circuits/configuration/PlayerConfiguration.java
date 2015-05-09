package sdc.avoidingproblems.circuits.configuration;

import java.math.BigInteger;
import java.util.List;
import sdc.avoidingproblems.circuits.Circuit;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerConfiguration {

   private final Long MOD;
   private final Circuit circuit;
   private final List<String> players; // all the players except himself
   private final List<ValueAndMAC> inputs;
   private final List<BeaverTriple> triples;

   public PlayerConfiguration(Long MOD, Circuit circuit, List<String> players, List<ValueAndMAC> inputs, List<BeaverTriple> triples) {
      this.MOD = MOD;
      this.circuit = circuit;
      this.players = players;
      this.inputs = inputs;
      this.triples = triples;
   }

   public Long getMOD() {
      return MOD;
   }

   public Circuit getCircuit() {
      return circuit;
   }

   public List<String> getPlayers() {
      return players;
   }

   public List<ValueAndMAC> getInputs() {
      return inputs;
   }

   public List<BeaverTriple> getTriples() {
      return triples;
   }

   public class ValueAndMAC {

      private final BigInteger value;
      private final BigInteger MAC;

      public ValueAndMAC(BigInteger value, BigInteger MAC) {
         this.value = value;
         this.MAC = MAC;
      }

      public BigInteger getValue() {
         return value;
      }

      public BigInteger getMAC() {
         return MAC;
      }
   }

   public class BeaverTriple {

      private final ValueAndMAC a;
      private final ValueAndMAC b;
      private final ValueAndMAC c;

      public BeaverTriple(ValueAndMAC a, ValueAndMAC b, ValueAndMAC c) {
         this.a = a;
         this.b = b;
         this.c = c;
      }

      public ValueAndMAC getA() {
         return a;
      }

      public ValueAndMAC getB() {
         return b;
      }

      public ValueAndMAC getC() {
         return c;
      }
   }
}
