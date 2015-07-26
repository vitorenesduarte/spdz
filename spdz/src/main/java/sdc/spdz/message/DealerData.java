package sdc.spdz.message;

import java.util.List;
import sdc.spdz.algebra.FieldElement;
import sdc.spdz.algebra.mac.BatchCheckValues;
import sdc.spdz.algebra.mac.SimpleRepresentation;
import sdc.spdz.circuit.BeaverTriples;
import sdc.spdz.circuit.Circuit;
import sdc.spdz.player.PlayerInfo;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class DealerData {

    private Circuit circuit;
    private List<SimpleRepresentation> inputs;
    private FieldElement alpha;
    private BeaverTriples beaverTriples;
    private List<PlayerInfo> otherPlayers;
    private BatchCheckValues batchCheckValues;

    public DealerData() {
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public List<SimpleRepresentation> getInputs() {
        return inputs;
    }

    public void setInputs(List<SimpleRepresentation> inputs) {
        this.inputs = inputs;
    }

    public FieldElement getAlpha() {
        return alpha;
    }

    public void setAlpha(FieldElement alpha) {
        this.alpha = alpha;
    }

    public BeaverTriples getBeaverTriples() {
        return beaverTriples;
    }

    public void setBeaverTriples(BeaverTriples beaverTriples) {
        this.beaverTriples = beaverTriples;
    }

    public List<PlayerInfo> getOtherPlayers() {
        return otherPlayers;
    }

    public void setOtherPlayers(List<PlayerInfo> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    public BatchCheckValues getBatchCheckValues() {
        return batchCheckValues;
    }

    public void setBatchCheckValues(BatchCheckValues batchCheckValues) {
        this.batchCheckValues = batchCheckValues;
    }

    public void setAll(DealerData data) {
        this.circuit = data.getCircuit();
        this.inputs = data.getInputs();
        this.alpha = data.getAlpha();
        this.beaverTriples = data.getBeaverTriples();
        this.otherPlayers = data.getOtherPlayers();
        this.batchCheckValues = data.getBatchCheckValues();
    }
}
