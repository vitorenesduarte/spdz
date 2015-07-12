package sdc.avoidingproblems.dealer;

import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sdc.avoidingproblems.ArgumentUtil;
import sdc.avoidingproblems.JSONManager;
import sdc.avoidingproblems.algebra.BeaverTriple;
import sdc.avoidingproblems.algebra.Field;
import sdc.avoidingproblems.algebra.FieldElement;
import sdc.avoidingproblems.algebra.mac.BatchCheckValues;
import sdc.avoidingproblems.algebra.mac.ExtendedRepresentation;
import sdc.avoidingproblems.algebra.mac.ExtendedRepresentationWithSum;
import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuit.BeaverTriples;
import sdc.avoidingproblems.circuit.Circuit;
import sdc.avoidingproblems.circuit.CircuitGenerator;
import sdc.avoidingproblems.circuit.CircuitParser;
import sdc.avoidingproblems.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.exception.InvalidParamException;
import sdc.avoidingproblems.message.DealerData;
import sdc.avoidingproblems.player.PlayerInfo;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Dealer {

    private static final BigInteger MOD = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

    private static Integer NPLAYERS;
    private static Integer NINPUTS;
    private static Field FIELD;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            File circuitFile = null;
            Boolean generateCircuit = false;
            Integer numberOfCircuitInputs = null;
            String[] players = null;

            for (String arg : args) {
                if (arg.startsWith("--circuit=")) {
                    circuitFile = ArgumentUtil.getValueAsFile(arg);
                } else if (arg.startsWith("--generate-circuit=")) {
                    generateCircuit = ArgumentUtil.getValueAsBoolean(arg);
                } else if (arg.startsWith("--circuit-inputs-number=")) {
                    numberOfCircuitInputs = ArgumentUtil.getValueAsInteger(arg);
                } else if (arg.startsWith("--players=")) {
                    players = ArgumentUtil.getValueAsArray(arg);
                }
            }

            if ((circuitFile == null && !generateCircuit) || (numberOfCircuitInputs == null && generateCircuit) || players == null) {
                System.err.println("Arguments missing:");
                System.err.println("--circuit=<path to circuit file>");
                System.err.println("--generate-circuit=<booelan>");
                System.err.println("--circuit-inputs-number=<number of inputs>");
                System.err.println("--players=<comma separated list of players host:port>");
                return;
            }

            Circuit circuit = generateCircuit ? CircuitGenerator.generate(numberOfCircuitInputs) : CircuitParser.parseFromFile(circuitFile);
            createPreprocessedData(circuit, players);

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        System.out.println("TIME: " + (System.currentTimeMillis() - start));
    }

    private static void createPreprocessedData(Circuit circuit, String[] playersHost) throws IOException {
        NINPUTS = circuit.getNumberOfInputs();
        NPLAYERS = playersHost.length;
        FIELD = new Field(MOD);

        // generate fixed mac key its shares
        // α = α_1 + ... + α_n
        final FieldElement fixedMACKey = FIELD.random();
        FieldElement[] alphas = FIELD.createShares(fixedMACKey, NPLAYERS);

        // generate random inputs for the circuit
        // (x, γ(x))
        List<SimpleRepresentation> inputs = generateRandomInputs(fixedMACKey);

        try {
            System.out.println("SINGLE-PARTY:");
            System.out.println("RESULT: " + circuit.eval(inputs));
        } catch (InvalidParamException | ExecutionModeNotSupportedException e) {
            e.printStackTrace(System.err);
        }

        // create shares for all the circuit's inputs
        // number of shares == number of players
        // (x_i, γ(x)_i)
        List<List<SimpleRepresentation>> inputShares = generateRandomInputShares(inputs);

        // create random beaver triples for all multiplication gates
        BeaverTriple[] beaverTriples = generateRandomBeaverTriples(circuit.getMultiplicationGatesCount(), fixedMACKey);

        // create shares of all beaver triples
        BeaverTriples[] beaverTriplesList = generateRandomBeaverTriplesShares(beaverTriples);

        // create betas for all players
        FieldElement[] betas = generateRandomBetas();

        // this is the u that will be used as random e (e_i = u^i)
        ExtendedRepresentation[] u = generateRandomExtendedRepresentationOfU(betas, playersHost);

        // create an extended representation to be used in the commit of each player
        ExtendedRepresentationWithSum[] myCommit = new ExtendedRepresentationWithSum[NPLAYERS];
        Map<String, ExtendedRepresentation>[] theirCommit = new Map[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            theirCommit[i] = new HashMap();
        }
        for (int p = 0; p < NPLAYERS; p++) {
            FieldElement s = FIELD.random();
            FieldElement[] sShares = FIELD.createShares(s, NPLAYERS);
            FieldElement[][] sMACs = new FieldElement[NPLAYERS][NPLAYERS];
            for (int i = 0; i < NPLAYERS; i++) {
                FieldElement mac = betas[i].mult(s);
                FieldElement[] macShares = FIELD.createShares(mac, NPLAYERS);
                for (int j = 0; j < NPLAYERS; j++) {
                    sMACs[i][j] = macShares[j];
                }
            }

            for (int i = 0; i < NPLAYERS; i++) {
                Map<String, FieldElement> playerToMAC = new HashMap();
                for (int j = 0; j < NPLAYERS; j++) {
                    playerToMAC.put(playersHost[j], sMACs[j][i]);
                }
                if (i == p) {
                    myCommit[i] = new ExtendedRepresentationWithSum(betas[i], sShares[i], s, playerToMAC);
                } else {
                    ExtendedRepresentation s_i = new ExtendedRepresentation(betas[i], sShares[i], playerToMAC);
                    theirCommit[i].put(playersHost[p], s_i);
                }
            }
        }

        // create the batch check values for all players
        BatchCheckValues[] batchCheckValues = new BatchCheckValues[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            batchCheckValues[i] = new BatchCheckValues(u[i], myCommit[i], theirCommit[i]);
        }

        ArrayList<PlayerInfo> playersID = new ArrayList();
        for (int i = 0; i < NPLAYERS; i++) {
            playersID.add(new PlayerInfo(playersHost[i]));
        }

        // send all data to players
        for (int i = 0; i < NPLAYERS; i++) {
            List<PlayerInfo> playersIDCopy = new ArrayList(playersID);
            playersIDCopy.remove(playersID.get(i));

            DealerData data = new DealerData();
            data.setCircuit(circuit);
            data.setAlpha(alphas[i]);
            data.setInputs(inputShares.get(i));
            data.setBeaverTriples(beaverTriplesList[i]);
            data.setBatchCheckValues(batchCheckValues[i]);
            data.setOtherPlayers(playersIDCopy);

            String[] parts = playersHost[i].split(":");

            try (Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
                    JsonWriter writer = new JsonWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {

                JSONManager.toJSON(data, DealerData.class, writer);
            }
        }

        for (int i = 0; i < NPLAYERS; i++) {
            String[] parts = playersHost[i].split(":");

            try (Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                writer.println("GO");
            }
        }
    }

    private static List<SimpleRepresentation> generateRandomInputs(FieldElement fixedMACKey) {
        List<SimpleRepresentation> inputs = new ArrayList(NINPUTS);

        for (int i = 0; i < NINPUTS; i++) {
            FieldElement input = FIELD.random();
            inputs.add(new SimpleRepresentation(input, input.mult(fixedMACKey)));
        }

        return inputs;
    }

    private static List<List<SimpleRepresentation>> generateRandomInputShares(List<SimpleRepresentation> inputs) {
        List<List<SimpleRepresentation>> inputShares = new ArrayList<>(NPLAYERS);

        for (int i = 0; i < NPLAYERS; i++) {
            inputShares.add(new ArrayList<SimpleRepresentation>());
        }

        for (int i = 0; i < NINPUTS; i++) {
            SimpleRepresentation[] shares = FIELD.createShares(inputs.get(i), NPLAYERS);
            for (int j = 0; j < NPLAYERS; j++) {
                inputShares.get(j).add(shares[j]);
            }
        }

        return inputShares;
    }

    private static BeaverTriple[] generateRandomBeaverTriples(int numberOfMultiplications, FieldElement fixedMACKey) {
        BeaverTriple[] beaverTriples = new BeaverTriple[numberOfMultiplications];
        for (int i = 0; i < numberOfMultiplications; i++) {
            beaverTriples[i] = FIELD.randomMultiplicationTriple(fixedMACKey);
        }

        return beaverTriples;
    }

    private static BeaverTriples[] generateRandomBeaverTriplesShares(BeaverTriple[] beaverTriples) {
        BeaverTriples[] beaverTriplesShares = new BeaverTriples[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            beaverTriplesShares[i] = new BeaverTriples();
        }

        // create shares for all multiplication triples previously generated
        for (BeaverTriple beaverTriple : beaverTriples) {
            SimpleRepresentation[] aShares = FIELD.createShares(beaverTriple.getA(), NPLAYERS);
            SimpleRepresentation[] bShares = FIELD.createShares(beaverTriple.getB(), NPLAYERS);
            SimpleRepresentation[] cShares = FIELD.createShares(beaverTriple.getC(), NPLAYERS);
            for (int j = 0; j < NPLAYERS; j++) {
                beaverTriplesShares[j].add(new BeaverTriple(aShares[j], bShares[j], cShares[j]));
            }
        }

        return beaverTriplesShares;
    }

    private static FieldElement[] generateRandomBetas() {
        FieldElement[] betas = new FieldElement[NPLAYERS];

        for (int i = 0; i < NPLAYERS; i++) {
            betas[i] = FIELD.random();
        }

        return betas;
    }

    private static ExtendedRepresentation[] generateRandomExtendedRepresentationOfU(FieldElement[] betas, String[] playersHost) {
        // the u
        FieldElement u = FIELD.random();
        FieldElement[] uShares = FIELD.createShares(u, NPLAYERS);
        // the macs that allow the u to be reliably opened
        FieldElement[][] uMACs = new FieldElement[NPLAYERS][NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            FieldElement mac = betas[i].mult(u);
            FieldElement[] macShares = FIELD.createShares(mac, NPLAYERS);
            for (int j = 0; j < NPLAYERS; j++) {
                uMACs[i][j] = macShares[j];
            }
        }

        // the u_s
        ExtendedRepresentation[] u_i_s = new ExtendedRepresentation[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            Map<String, FieldElement> playerToMAC = new HashMap();
            for (int j = 0; j < NPLAYERS; j++) {
                playerToMAC.put(playersHost[j], uMACs[j][i]);
            }
            ExtendedRepresentation u_i = new ExtendedRepresentation(betas[i], uShares[i], playerToMAC);
            u_i_s[i] = u_i;
        }

        return u_i_s;
    }
}
