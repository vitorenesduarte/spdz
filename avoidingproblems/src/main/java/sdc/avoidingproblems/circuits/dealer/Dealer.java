package sdc.avoidingproblems.circuits.dealer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.CircuitGenerator;
import sdc.avoidingproblems.circuits.BeaverTriples;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.BigIntegerFE;
import sdc.avoidingproblems.circuits.algebra.Field;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.configuration.DealerConfiguration;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.player.SharedInputs;

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

            try {

                Circuit circuit = CircuitGenerator.generate(configuration.getNumberOfInputs());
                Field field = new Field(configuration.getMOD());
                Class<?> clazz = BigIntegerFE.class;
                FieldElement fixedMACKey = field.random(clazz);

                if (configuration.getGenerateInputs()) {
                    // for now we'll assume this is always true
                }

                List<FieldElement> inputs = new ArrayList(configuration.getNumberOfInputs());
                for (int i = 0; i < configuration.getNumberOfInputs(); i++) {
                    inputs.add(field.random(clazz));
                }

                Integer NPLAYERS = configuration.getPlayers().size();

            // create shares for all the circuit's inputs
                // number of shares == number of players
                SharedInputs[] inputShares = new SharedInputs[NPLAYERS];
                for (int i = 0; i < NPLAYERS; i++) {
                    inputShares[i] = new SharedInputs(configuration.getNumberOfInputs());
                }

                for (int i = 0; i < configuration.getNumberOfInputs(); i++) {
                    SimpleRepresentation vam = new SimpleRepresentation(inputs.get(i), inputs.get(i).mult(fixedMACKey));
                    SimpleRepresentation[] shares = field.createShares(vam, NPLAYERS);
                    for (int j = 0; j < NPLAYERS; j++) {
                        inputShares[j].add(shares[j]);
                    }
                }

                // create random multiplication triples for all multiplication gates
                int numberOfMultiplications = circuit.getMultiplicationGatesCount();
                BeaverTriple[] multiplicationTriples = new BeaverTriple[numberOfMultiplications];
                for (int i = 0; i < numberOfMultiplications; i++) {
                    multiplicationTriples[i] = field.randomMultiplicationTriple(clazz, fixedMACKey);
                }

                // init all pre processed data
                BeaverTriples[] preProcessedData = new BeaverTriples[NPLAYERS];
                for (int i = 0; i < NPLAYERS; i++) {
                    preProcessedData[i] = new BeaverTriples();
                }

                // create shares for all multiplication triples previously generated
                for (int i = 0; i < numberOfMultiplications; i++) {
                    SimpleRepresentation[] aShares = field.createShares(multiplicationTriples[i].getA(), NPLAYERS);
                    SimpleRepresentation[] bShares = field.createShares(multiplicationTriples[i].getB(), NPLAYERS);
                    SimpleRepresentation[] cShares = field.createShares(multiplicationTriples[i].getC(), NPLAYERS);
                    for (int j = 0; j < NPLAYERS; j++) {
                        preProcessedData[j].add(new BeaverTriple(aShares[j], bShares[j], cShares[j]));
                    }
                }

            } catch (ClassNotSupportedException ex) {
                Logger.getLogger(Dealer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
