package sdc.spdz.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.spdz.exception.ExecutionModeNotSupportedException;
import sdc.spdz.exception.InvalidPlayersException;
import sdc.spdz.circuit.Circuit;
import static sdc.spdz.circuit.ExecutionMode.DISTRIBUTED;
import static sdc.spdz.circuit.ExecutionMode.LOCAL;
import sdc.spdz.circuit.Gate;
import sdc.spdz.circuit.GateSemantic;
import static sdc.spdz.circuit.GateSemantic.MULT;
import static sdc.spdz.circuit.GateSemantic.PLUS;
import sdc.spdz.circuit.BeaverTriples;
import sdc.spdz.algebra.BeaverTriple;
import sdc.spdz.algebra.FieldElement;
import sdc.spdz.algebra.Function;
import sdc.spdz.algebra.mac.BatchCheckValues;
import sdc.spdz.algebra.mac.ExtendedRepresentation;
import sdc.spdz.algebra.mac.ExtendedRepresentationWithSum;
import sdc.spdz.algebra.mac.SimpleRepresentation;
import sdc.spdz.algebra.mac.ToBeMACChecked;
import sdc.spdz.exception.ClassNotSupportedException;
import sdc.spdz.exception.InvalidParamException;
import sdc.spdz.exception.OperationNotSupportedException;
import sdc.spdz.message.Commit;
import sdc.spdz.message.DealerData;
import sdc.spdz.message.MessageManager;
import sdc.spdz.message.MultiplicationShare;
import sdc.spdz.message.Open;
import sdc.spdz.message.OpenCommited;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Player extends Thread {

    private static final Logger logger = Logger.getLogger(Player.class.getName());
    private Long countDistributedMultiplications = 0L;

    private final PlayerInfo playerInfo;
    private final BigInteger MOD = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");
    private Circuit circuit;
    private List<SimpleRepresentation> inputs;
    private FieldElement alpha;
    private BeaverTriples beaverTriples;
    private List<PlayerInfo> players;
    private BatchCheckValues batchCheckValues;

    private final Inbox inbox;
    private final List<ToBeMACChecked> toBeMACChecked;
    private final Map<String, PrintWriter> writers;

    public Player(String host, int port) {
        playerInfo = new PlayerInfo(host, port);
        inbox = new Inbox();
        toBeMACChecked = new ArrayList<>();
        players = new ArrayList<>();
        writers = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            DealerData dealerData = new DealerData();
            Semaphore dealerIsDone = new Semaphore(0);
            Semaphore PLAYER_END = new Semaphore(0);
            Semaphore ACCEPTOR_END = new Semaphore(0);
            new Acceptor(playerInfo.getPort(), inbox, dealerIsDone, dealerData, PLAYER_END, ACCEPTOR_END).start();
            logger.info("waiting for dealer...");
            dealerIsDone.acquire();

            setParams(dealerData);
            checkParams();
            connectWithPlayers();

            long start = System.currentTimeMillis();

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
                out("TIME: " + (System.currentTimeMillis() - start));
            }

            PLAYER_END.release();
            ACCEPTOR_END.acquire();
        } catch (IOException | InvalidParamException | InvalidPlayersException | InterruptedException | ClassNotSupportedException | ExecutionModeNotSupportedException | OperationNotSupportedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void setParams(DealerData dealerData) {
        this.circuit = dealerData.getCircuit();
        this.inputs = dealerData.getInputs();
        this.alpha = dealerData.getAlpha();
        this.beaverTriples = dealerData.getBeaverTriples();
        this.players = dealerData.getOtherPlayers();
        this.batchCheckValues = dealerData.getBatchCheckValues();
    }

    private void checkParams() throws InvalidParamException, InvalidPlayersException {
        if (circuit == null) {
            throw new InvalidParamException("Circuit Not Found");
        }
        if (inputs == null) {
            throw new InvalidParamException("Inputs Not Found");
        }
        if (circuit.getNumberOfInputs() != inputs.size()) {
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

    private void connectWithPlayers() throws IOException {
        for (PlayerInfo pID : players) {
            writers.put(pID.getHostAndPort(), new PrintWriter(new Socket(pID.getHost(), pID.getPort()).getOutputStream(), true));
        }
    }

    private List<SimpleRepresentation> initEdgesValues() {
        List<SimpleRepresentation> edgesValues = new ArrayList(inputs.size() + circuit.getGateCount());
        for (SimpleRepresentation vam : inputs) {
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
        FieldElement e = u.pow(0);
        SimpleRepresentation y = toBeMACChecked.get(0).getShare().mult(e); // y*u^0 = y
        FieldElement y_ = toBeMACChecked.get(0).getOpenedValue().mult(e);
        for (int power = 1; power < toBeMACChecked.size(); power++) {
            SimpleRepresentation share = toBeMACChecked.get(power).getShare();
            FieldElement openedValue = toBeMACChecked.get(power).getOpenedValue();

            e = e.mult(u);
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
        writers.get(player.getHostAndPort()).println(message);
    }

    private void sendToPlayers(String message) {
        for (PlayerInfo player : players) {
            sendToPlayer(message, player);
        }
    }

    private void out(String s) {
        logger.info(s);
    }
}
