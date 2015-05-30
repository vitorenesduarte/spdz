package sdc.avoidingproblems.circuits.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidPlayersException;
import sdc.avoidingproblems.circuits.Circuit;
import static sdc.avoidingproblems.circuits.ExecutionMode.DISTRIBUTED;
import static sdc.avoidingproblems.circuits.ExecutionMode.LOCAL;
import sdc.avoidingproblems.circuits.Gate;
import sdc.avoidingproblems.circuits.GateSemantic;
import static sdc.avoidingproblems.circuits.GateSemantic.MULT;
import static sdc.avoidingproblems.circuits.GateSemantic.PLUS;
import sdc.avoidingproblems.circuits.PreProcessedData;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.algebra.Util;
import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.algebra.mac.ToBeMACChecked;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.OperationNotSupportedException;
import sdc.avoidingproblems.circuits.message.MessageManager;
import sdc.avoidingproblems.circuits.message.MultiplicationShare;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Player extends Thread {

    private static final Logger logger = Logger.getLogger(Player.class.getName());
    private Long countDistributedMultiplications = 0L;

    private final PlayerInfo playerInfo;
    private Circuit circuit;
    private List<SimpleRepresentation> sharedInputs;
    private Long MOD;
    private List<PlayerInfo> players;
    private Integer NUMBER_OF_OTHER_PLAYERS;
    private PreProcessedData preProcessedData;
    private final List<SimpleRepresentation> sumAll;

    private final Inbox inbox;
    private final List<ToBeMACChecked> toBeMACChecked;

    public Player(int UID, String host, int port, List<SimpleRepresentation> sumAll) {
        this.playerInfo = new PlayerInfo(UID, host, port);
        this.sumAll = sumAll;
        inbox = new Inbox(playerInfo);
        toBeMACChecked = new ArrayList();
    }

    public PlayerInfo getInfo() {
        return playerInfo;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public void setInputs(List<SimpleRepresentation> sharedInputs) {
        this.sharedInputs = sharedInputs;
    }

    public void setMOD(Long MOD) {
        this.MOD = MOD;
    }

    public void setPreProcessedData(PreProcessedData preProcessedData) {
        this.preProcessedData = preProcessedData;
    }

    public void setPlayers(List<PlayerInfo> otherPlayers) {
        this.players = otherPlayers;
        this.NUMBER_OF_OTHER_PLAYERS = otherPlayers.size();
    }

    @Override
    public void run() {
        try {
            checkParams();
            checkPreProcessedData();
            checkPlayers();
            InboxReader reader = new InboxReader(playerInfo.getPort(), inbox);
            reader.start();
            Thread.sleep(1000);

            List<SimpleRepresentation> edgesValues = initEdgesValues();

            for (Gate gate : circuit.getGates()) {
                List<Integer> inputEdges = gate.getInputEdges();
                SimpleRepresentation[] params = new SimpleRepresentation[inputEdges.size()];
                for (int j = 0; j < inputEdges.size(); j++) {
                    params[j] = edgesValues.get(inputEdges.get(j));
                }

                SimpleRepresentation result;
                GateSemantic semantic = gate.getSemantic();

                switch (semantic) {
                    case MULT:
                        countDistributedMultiplications++;
                        result = evalDistributedMult(params[0], params[1], preProcessedData.consume());
                        break;
                    case PLUS:
                        result = GateSemantic.getFunction(semantic).apply(LOCAL, null, null, null, params);
                        break;
                    default:
                        throw new OperationNotSupportedException();
                }
                edgesValues.add(result);
            }

            SimpleRepresentation result = edgesValues.get(edgesValues.size() - 1);
            sumAll.add(result);
            
            out("RES : " + result);
        } catch (InvalidParamException | InvalidPlayersException | InterruptedException | ClassNotSupportedException | ExecutionModeNotSupportedException | OperationNotSupportedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void checkParams() throws InvalidParamException {
        if (circuit == null) {
            throw new InvalidParamException("Circuit Not Found");
        }
        if (sharedInputs == null) {
            throw new InvalidParamException("Inputs Not Found");
        }
        if (circuit.getInputSize() != sharedInputs.size()) {
            throw new InvalidParamException("Circuit's number of inputs is different from inputs lenght");
        }
        if (MOD == null) {
            throw new InvalidParamException("MOD Not Found");
        }
    }

    private void checkPreProcessedData() throws InvalidParamException {
        if (preProcessedData == null) {
            throw new InvalidParamException("Pre Processed Data Not Found");
        }
    }

    private void checkPlayers() throws InvalidPlayersException, InvalidParamException {
        if (players == null) {
            throw new InvalidParamException("Players Not Found");
        }
        for (PlayerInfo pID : players) {
            if (pID.equals(playerInfo)) {
                throw new InvalidPlayersException(); // I cannot multi party with myself
            }
        }
    }

    private List<SimpleRepresentation> initEdgesValues() {
        List<SimpleRepresentation> edgesValues = new ArrayList(sharedInputs.size() + circuit.getGateCount());
        for (SimpleRepresentation vam : sharedInputs) {
            edgesValues.add(vam);
        }
        return edgesValues;
    }

    private SimpleRepresentation evalDistributedMult(SimpleRepresentation x, SimpleRepresentation y, BeaverTriple triple) throws InterruptedException, InvalidParamException, ExecutionModeNotSupportedException, ClassNotSupportedException {
        SimpleRepresentation dShared = x.sub(triple.getA());
        SimpleRepresentation eShared = y.sub(triple.getB());

        String message = MessageManager.createMessage(new MultiplicationShare(countDistributedMultiplications, dShared.getValue().longValue(), eShared.getValue().longValue()));
        sendToPlayers(message);

        List<MultiplicationShare> messages = inbox.waitForMultiplicationShares(countDistributedMultiplications, NUMBER_OF_OTHER_PLAYERS);
        if(messages.size() != NUMBER_OF_OTHER_PLAYERS){
            out("SOMETHING IS WRONG : " + messages);
        }
        FieldElement dOpened = Util.getFieldElementInstance(dShared.getValue().getClass(), dShared.getValue().longValue(), MOD);
        FieldElement eOpened = Util.getFieldElementInstance(eShared.getValue().getClass(), eShared.getValue().longValue(), MOD);

        for (MultiplicationShare multShare : messages) {
            dOpened = dOpened.add(multShare.getD());
            eOpened = eOpened.add(multShare.getE());
        }
        toBeMACChecked.add(new ToBeMACChecked(dShared, dOpened));
        toBeMACChecked.add(new ToBeMACChecked(eShared, eOpened));

        Function f = GateSemantic.getFunction(MULT);
        SimpleRepresentation result = f.apply(DISTRIBUTED, triple, dOpened, eOpened, dShared);
        return result;
    }

    private void sendToPlayers(String message) {
        try {
            for (PlayerInfo player : players) {
                try (
                        Socket socket = new Socket(player.getHost(), player.getPort());
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.write(message);
                }
            }
        } catch (IOException ex) {
            logger.info(message);
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void out(String s) {
        System.out.println(playerInfo.getUID() + " - " + s);
    }

}
