package sdc.avoidingproblems.circuit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class CircuitParser {

    private static final Logger logger = Logger.getLogger(CircuitParser.class.getName());

    public static Circuit parseFromFile(File file) throws IOException {
        Circuit circuit;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        if (line != null) {
            Integer inputSize = Integer.parseInt(line);
            circuit = new Circuit(inputSize);
        } else {
            return null;
        }
        
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "+":
                    circuit.addGate(new Gate(GateSemantic.PLUS, getInputEdges(parts)));
                    break;
                case "x":
                    circuit.addGate(new Gate(GateSemantic.MULT, getInputEdges(parts)));
                    break;
                default:
                    logger.log(Level.WARNING, "{0}: unknown gate", parts[0]);
                    break;
            }
        }

        return circuit;
    }

    private static List<Integer> getInputEdges(String[] parts) {
        List<Integer> inputEdges = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            inputEdges.add(Integer.parseInt(parts[i]));
        }

        return inputEdges;
    }
}
