package sdc.avoidingproblems.circuits.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import sdc.avoidingproblems.circuits.BeaverTriples;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.algebra.mac.BatchCheckValues;
import sdc.avoidingproblems.circuits.algebra.mac.ExtendedRepresentation;
import sdc.avoidingproblems.circuits.algebra.mac.ExtendedRepresentationWithSum;
import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.algebra.mac.ToBeMACChecked;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;
import sdc.avoidingproblems.circuits.exception.OperationNotSupportedException;
import sdc.avoidingproblems.circuits.message.Commit;
import sdc.avoidingproblems.circuits.message.MessageManager;
import sdc.avoidingproblems.circuits.message.MultiplicationShare;
import sdc.avoidingproblems.circuits.message.Open;
import sdc.avoidingproblems.circuits.message.OpenCommited;

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
    private BigInteger MOD;
    private FieldElement alpha;
    private List<PlayerInfo> players;
    private Integer NUMBER_OF_OTHER_PLAYERS;
    private BeaverTriples beaverTriples;
    private BatchCheckValues batchCheckValues;

    private final Inbox inbox;
    private final List<ToBeMACChecked> toBeMACChecked;

    public Player(int UID, String host, int port) {
        this.playerInfo = new PlayerInfo(UID, host, port);
        inbox = new Inbox();
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

    public void setMOD(BigInteger MOD) {
        this.MOD = MOD;
    }

    public void setAlpha(FieldElement alpha) {
        this.alpha = alpha;
    }

    public void setBeaverTriples(BeaverTriples beaverTriples) {
        this.beaverTriples = beaverTriples;
    }

    public void setPlayers(List<PlayerInfo> otherPlayers) {
        this.players = otherPlayers;
        this.NUMBER_OF_OTHER_PLAYERS = otherPlayers.size();
    }

    public void setBatchCheckValues(BatchCheckValues batchCheckValues) {
        this.batchCheckValues = batchCheckValues;
    }

    @Override
    public void run() {
        try {
            checkParams();

            inbox.setNumberOfOtherPlayer(NUMBER_OF_OTHER_PLAYERS);
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
                        result = evalDistributedMult(params[0], params[1], beaverTriples.consume());
                        break;
                    case PLUS:
                        result = GateSemantic.getFunction(semantic).apply(LOCAL, null, null, null, params);
                        break;
                    default:
                        throw new OperationNotSupportedException();
                }
                edgesValues.add(result);
            }
            Boolean checked = countDistributedMultiplications > 0 ? doBatchCheck() : true;
            if (checked) {
                openFinalResult(edgesValues.get(edgesValues.size() - 1).getValue());
            }
        } catch (InvalidParamException | InvalidPlayersException | InterruptedException | ClassNotSupportedException | ExecutionModeNotSupportedException | OperationNotSupportedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void checkParams() throws InvalidParamException, InvalidPlayersException {
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
        if (alpha == null) {
            throw new InvalidParamException("Alpha Not Found");
        }
        if (beaverTriples == null) {
            throw new InvalidParamException("Beaver Triples Not Found");
        }
        if (batchCheckValues == null) {
            throw new InvalidParamException("Batch Check Values Not Found");
        }
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

        String message = MessageManager.createMessage(new MultiplicationShare(countDistributedMultiplications, dShared.getValue().bigIntegerValue(), eShared.getValue().bigIntegerValue()));
        sendToPlayers(message);

        List<MultiplicationShare> messages = inbox.waitForMultiplicationShares(countDistributedMultiplications);
        if (messages.size() != NUMBER_OF_OTHER_PLAYERS) {
            out("SOMETHING IS WRONG : " + messages);
        }
        FieldElement dOpened = dShared.getValue();
        FieldElement eOpened = eShared.getValue();

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

    private Boolean doBatchCheck() throws InterruptedException, ClassNotSupportedException {
        FieldElement u = open(batchCheckValues.getU());
        if (u == null) {
            return false;
        }
        SimpleRepresentation y = toBeMACChecked.get(0).getShare(); // y*u^0 = y
        FieldElement y_ = toBeMACChecked.get(0).getOpenedValue();
        for (int power = 1; power < toBeMACChecked.size(); power++) {
            SimpleRepresentation share = toBeMACChecked.get(power).getShare();
            FieldElement openedValue = toBeMACChecked.get(power).getOpenedValue();

            FieldElement e = u.pow(power);
            y = y.add(share.mult(e));
            y_ = y_.add(openedValue.mult(e));
        }
        FieldElement d = alpha.mult(y_).sub(y.getMAC());
        commit(d);
        List<Commit> commitList = inbox.waitForCommit();

        openMyCommitedValueAndTheirs();
        List<OpenCommited> openCommitedList = inbox.waitForOpenCommited();

        // check my commit
        ExtendedRepresentationWithSum s = batchCheckValues.getMyCommit();
        List<OpenCommited> myOpenCommited = new ArrayList();
        for (OpenCommited oc : openCommitedList) {
            if (oc.getPlayer().equals(playerInfo.getHostAndPort())) {
                myOpenCommited.add(oc);
            }
        }
        FieldElement openedValue = s.getValue();
        FieldElement macToBeChecked = s.getMAC(playerInfo.getHostAndPort());
        for (OpenCommited oc : myOpenCommited) {
            openedValue = openedValue.add(oc.getValue());
            macToBeChecked = macToBeChecked.add(oc.getMAC());
        }

        FieldElement mac = s.getBeta().mult(openedValue);

        if (mac.bigIntegerValue().compareTo(macToBeChecked.bigIntegerValue()) != 0) {
            out("MAC DOES NOT CHECK!");
            return false;
        } else {
            out("MY COMMIT MAC CHECKED");
        }

        // check their commit
        Map<String, ExtendedRepresentation> theirCommit = batchCheckValues.getTheirCommit();
        for (String player : theirCommit.keySet()) {
            List<OpenCommited> thisPlayerOpenCommited = new ArrayList();
            for (OpenCommited oc : openCommitedList) { // optimize this
                if (oc.getPlayer().equals(player)) {
                    thisPlayerOpenCommited.add(oc);
                }
            }
            ExtendedRepresentation r = theirCommit.get(player);
            FieldElement thisPlayerOpenedValue = r.getValue();
            FieldElement thisPlayerMacToBeChecked = r.getMAC(playerInfo.getHostAndPort());
            for (OpenCommited oc : thisPlayerOpenCommited) {
                thisPlayerOpenedValue = thisPlayerOpenedValue.add(oc.getValue());
                thisPlayerMacToBeChecked = thisPlayerMacToBeChecked.add(oc.getMAC());
            }

            FieldElement thisPlayerMac = r.getBeta().mult(thisPlayerOpenedValue);

            if (thisPlayerMac.bigIntegerValue().compareTo(thisPlayerMacToBeChecked.bigIntegerValue()) != 0) {
                out("MAC DOES NOT CHECK!");
                return false;
            } else {
                out("THEIR COMMIT MAC CHECKED");
            }
        }
        return true;
    }

    private FieldElement open(ExtendedRepresentation value) throws InterruptedException {
        for (PlayerInfo player : players) {
            String key = player.getHostAndPort();
            FieldElement mac = value.getMAC(key);
            Open open = new Open(value.getValue().bigIntegerValue(), mac.bigIntegerValue());
            String message = MessageManager.createMessage(open);
            sendToPlayer(message, player);
        }

        List<Open> openList = inbox.waitForOpen();
        FieldElement openedValue = value.getValue();
        FieldElement macToBeChecked = value.getMAC(playerInfo.getHostAndPort());
        for (Open open : openList) {
            openedValue = openedValue.add(open.getValue());
            macToBeChecked = macToBeChecked.add(open.getMAC());
        }

        FieldElement mac = value.getBeta().mult(openedValue);
        if (mac.bigIntegerValue().compareTo(macToBeChecked.bigIntegerValue()) != 0) {
            out("MAC DOES NOT CHECK!");
            return null;
        } else {
            out("U MAC CHECKED");
        }

        return openedValue;
    }

    private void commit(FieldElement value) throws InterruptedException {
        ExtendedRepresentationWithSum s = batchCheckValues.getMyCommit();
        FieldElement commit = value.sub(s.getSum());
        String message = MessageManager.createMessage(new Commit(playerInfo.getHostAndPort(), commit.bigIntegerValue()));
        sendToPlayers(message);
    }

    private void openMyCommitedValueAndTheirs() {
        ExtendedRepresentationWithSum s = batchCheckValues.getMyCommit();
        for (PlayerInfo player : players) {
            String key = player.getHostAndPort();
            FieldElement mac = s.getMAC(key);
            OpenCommited openCommited = new OpenCommited(playerInfo.getHostAndPort(), s.getValue().bigIntegerValue(), mac.bigIntegerValue());
            String message = MessageManager.createMessage(openCommited);
            sendToPlayer(message, player);
        }

        Map<String, ExtendedRepresentation> theirCommit = batchCheckValues.getTheirCommit();
        for (String playerHostAndPort : theirCommit.keySet()) {
            ExtendedRepresentation r = theirCommit.get(playerHostAndPort);
            for (PlayerInfo player : players) {
                String key = player.getHostAndPort();
                FieldElement mac = r.getMAC(key);
                OpenCommited openCommited = new OpenCommited(playerHostAndPort, r.getValue().bigIntegerValue(), mac.bigIntegerValue());
                String message = MessageManager.createMessage(openCommited);
                sendToPlayer(message, player);
            }
        }
    }

    private void openFinalResult(FieldElement result) throws InterruptedException {
        String message = MessageManager.createMessage(new Open(result.bigIntegerValue(), BigInteger.ZERO));
        sendToPlayers(message);
        List<Open> openList = inbox.waitForOpen();
        for (Open open : openList) {
            result = result.add(open.getValue());
        }
        out(result.bigIntegerValue().toString());
    }

    private void sendToPlayer(String message, PlayerInfo player) {
        try {
            try (
                    Socket socket = new Socket(player.getHost(), player.getPort());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.write(message);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
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
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void out(String s) {
        System.out.println(playerInfo.getUID() + " - " + s);
    }

}
