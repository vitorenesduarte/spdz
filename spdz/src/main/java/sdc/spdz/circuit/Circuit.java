package sdc.spdz.circuit;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static sdc.spdz.circuit.ExecutionMode.LOCAL;
import sdc.spdz.algebra.FieldElement;
import sdc.spdz.algebra.mac.SimpleRepresentation;
import sdc.spdz.exception.ExecutionModeNotSupportedException;
import sdc.spdz.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Circuit {

    private final Integer numberOfInputs;
    private final List<Gate> gates;

    public Circuit(Integer numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
        this.gates = new ArrayList();
    }

    public Circuit(Integer numberOfInputs, List<Gate> gates) {
        this.gates = gates;
        this.numberOfInputs = numberOfInputs;
    }

    public Integer getNumberOfInputs() {
        return numberOfInputs;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public void addGate(Gate gate) {
        this.gates.add(gate);
    }

    public int getGateCount() {
        return gates.size();
    }

    public int getMultiplicationGatesCount() {
        int count = 0;
        for (Gate gate : gates) {
            if (gate.getSemantic().equals(GateSemantic.MULT)) {
                count++;
            }
        }

        return count;
    }

    public FieldElement eval(List<SimpleRepresentation> inputs) throws InvalidParamException, ExecutionModeNotSupportedException{
        for (Gate gate : gates) {
            List<Integer> inputEdges = gate.getInputEdges();
            SimpleRepresentation[] params = new SimpleRepresentation[inputEdges.size()];
            for (int j = 0; j < inputEdges.size(); j++) {
                params[j] = inputs.get(inputEdges.get(j));
            }

            inputs.add(GateSemantic.getFunction(gate.getSemantic()).apply(LOCAL, null, null, null, params));
        }

        return inputs.get(inputs.size() - 1).getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(numberOfInputs).append("\n");
        for (Gate gate : gates) {
            switch (gate.getSemantic()) {
                case PLUS:
                    sb.append("+ ").append(StringUtils.join(gate.getInputEdges(), " ")).append("\n");
                    break;
                case MULT:
                    sb.append("x ").append(StringUtils.join(gate.getInputEdges(), " ")).append("\n");
                    break;
            }
        }

        return sb.toString();
    }
}
