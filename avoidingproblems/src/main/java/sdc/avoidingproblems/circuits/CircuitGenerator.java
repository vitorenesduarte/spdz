package sdc.avoidingproblems.circuits;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class CircuitGenerator {

    public static Circuit generate(Integer NINPUTS) {
        SecureRandom random = new SecureRandom();
        Map<Integer, Gate> gates = new HashMap();
        List<Integer> notOccupied = new ArrayList();

        Integer edgeCount = NINPUTS;
        for (int i = 0; i < NINPUTS; i++) {
            notOccupied.add(i);
        }

        while (notOccupied.size() > 1) { // if notOccupied.size() == 1, the edge missing is the output of the circuit
            GateSemantic semantic = GateSemantic.getRandomGate();
            int i = random.nextInt(notOccupied.size());
            Integer edgeOne = notOccupied.get(i);
            notOccupied.remove(i);

            i = random.nextInt(notOccupied.size());
            Integer edgeTwo = notOccupied.get(i);
            notOccupied.remove(i);

            notOccupied.add(edgeCount++);

            List<Integer> inputEdges = new ArrayList(2);
            inputEdges.add(edgeOne);
            inputEdges.add(edgeTwo);
            Gate gate = new Gate(semantic, inputEdges);
            gates.put(edgeCount, gate);
        }

        Circuit circuit = new Circuit(NINPUTS, gates);
        return circuit;
    }
}
