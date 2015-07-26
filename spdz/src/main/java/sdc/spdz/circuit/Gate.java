package sdc.spdz.circuit;

import java.util.List;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Gate {

    private GateSemantic semantic;
    private List<Integer> inputEdges;

    public Gate(GateSemantic semantic, List<Integer> inputEdges) {
        this.semantic = semantic;
        this.inputEdges = inputEdges;
    }

    public GateSemantic getSemantic() {
        return semantic;
    }
    
    public void setSemantic(GateSemantic semantic){
        this.semantic = semantic;
    }

    public List<Integer> getInputEdges() {
        return inputEdges;
    }

    public void setInputEdges(List<Integer> inputEdges){
        this.inputEdges = inputEdges;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (semantic.equals(GateSemantic.MULT)) {
            sb.append("< x , ");
        } else if (semantic.equals(GateSemantic.PLUS)) {
            sb.append("< + , ");
        }

        sb.append(inputEdges.toString()).append(">");
        return sb.toString();
    }

}
