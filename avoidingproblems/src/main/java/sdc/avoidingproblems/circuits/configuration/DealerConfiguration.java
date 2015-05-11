package sdc.avoidingproblems.circuits.configuration;

import java.util.List;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class DealerConfiguration {
   // for now the dealer also generates the circuit and sends everything to the players;

   private final Integer numberOfInputs;
   private final Integer batchCheckSize;
   private final Boolean generateInputs;
   private final Long MOD;
   private final List<String> players; // host and port. e.g.: localhost:4567

   public DealerConfiguration(Integer numberOfInputs, Integer batchCheckSize, Boolean generateInputs, Long MOD, List<String> players) {
      this.numberOfInputs = numberOfInputs;
      this.batchCheckSize = batchCheckSize;
      this.generateInputs = generateInputs;
      this.MOD = MOD;
      this.players = players;
   }

   public Integer getNumberOfInputs() {
      return numberOfInputs;
   }

   public Integer getBatchCheckSize() {
      return batchCheckSize;
   }

   public Boolean getGenerateInputs() {
      return generateInputs;
   }

   public Long getMOD() {
      return MOD;
   }

   public List<String> getPlayers() {
      return players;
   }

   @Override
   public String toString() {
      return "DealerConfiguration{" + "numberOfInputs=" + numberOfInputs + ", batchCheckSize=" + batchCheckSize + ", generateInputs=" + generateInputs + ", MOD=" + MOD + ", players=" + players + '}';
   }
}
