package sdc.avoidingproblems.circuits.dealer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import sdc.avoidingproblems.circuits.configuration.DealerConfiguration;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Dealer {

   public static void main(String[] args) {
      if (args.length >= 2 && args[0].equals("-c")) {
         Gson gson = new Gson();
         BufferedReader br;
         try {
            br = new BufferedReader(
                    new FileReader(args[1]));
         } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            return;
         }

         DealerConfiguration configuration;
         try {
            configuration = gson.fromJson(br, DealerConfiguration.class);
         } catch (JsonSyntaxException ex) {
            System.out.println("Error parsing JSON file " + args[1]);
            return;
         }

         System.out.println(configuration);
      }
   }
}
