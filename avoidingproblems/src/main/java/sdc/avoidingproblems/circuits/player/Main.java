package sdc.avoidingproblems.circuits.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sdc.avoidingproblems.circuits.Circuit;
import sdc.avoidingproblems.circuits.CircuitGenerator;
import sdc.avoidingproblems.circuits.algebra.Field;
import sdc.avoidingproblems.circuits.BeaverTriples;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.BigIntegerFE;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.mac.BatchCheckValues;
import sdc.avoidingproblems.circuits.algebra.mac.ExtendedRepresentation;
import sdc.avoidingproblems.circuits.algebra.mac.ExtendedRepresentationWithSum;
import sdc.avoidingproblems.circuits.algebra.mac.SimpleRepresentation;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Main {

    public static void main(String[] args) throws ExecutionModeNotSupportedException, InterruptedException, ClassNotSupportedException, InvalidParamException {
        final Long MOD = 41L;
        final int PORT = 3000;
        final int NINPUTS = 30000;
        final int NPLAYERS = 3;
        final Field field = new Field(MOD);
        final Class<?> clazz = BigIntegerFE.class;
        final FieldElement fixedMACKey = field.random(clazz);

        // generate a random circuit
        final Circuit circuit = CircuitGenerator.generate(NINPUTS);

        //Jung.preview(circuit);
        //System.out.println(circuit.toString());
        int numberOfCommunications = NPLAYERS * (NPLAYERS - 1) * circuit.getMultiplicationGatesCount();
        System.out.println("Number of comunications : " + numberOfCommunications);
        System.out.println("Number of players : " + NPLAYERS);
        // generate random inputs for the circuit
        List<FieldElement> inputs = new ArrayList(NINPUTS);
        for (int i = 0; i < NINPUTS; i++) {
            inputs.add(field.random(clazz));
        }

        System.out.println("INPUTS: " + inputs.toString());
        System.out.println("FIXED MAC KEY: " + fixedMACKey);
        System.out.println("MOD " + MOD);
        System.out.println("SINGLE-PARTY:");
        FieldElement singePartyEvalResult = circuit.eval(inputs);
        System.out.println("RESULT: " + new SimpleRepresentation(singePartyEvalResult, singePartyEvalResult.mult(fixedMACKey)));

        System.out.println("MULTI-PARTY:");
        // create shares for all the circuit's inputs
        // number of shares == number of players
        SharedInputs[] inputShares = new SharedInputs[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            inputShares[i] = new SharedInputs(NINPUTS);
        }

        for (int i = 0; i < NINPUTS; i++) {
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
        BeaverTriples[] beaverTriples = new BeaverTriples[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            beaverTriples[i] = new BeaverTriples();
        }

        // create shares for all multiplication triples previously generated
        for (int i = 0; i < numberOfMultiplications; i++) {
            SimpleRepresentation[] aShares = field.createShares(multiplicationTriples[i].getA(), NPLAYERS);
            SimpleRepresentation[] bShares = field.createShares(multiplicationTriples[i].getB(), NPLAYERS);
            SimpleRepresentation[] cShares = field.createShares(multiplicationTriples[i].getC(), NPLAYERS);
            for (int j = 0; j < NPLAYERS; j++) {
                beaverTriples[j].add(new BeaverTriple(aShares[j], bShares[j], cShares[j]));
            }
        }

        // create betas for all players (this is the u that will be used as random e (e_i = u^i)
        FieldElement[] betas = new FieldElement[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            betas[i] = field.random(clazz);
        }

        // the u
        FieldElement u = field.random(clazz);
        FieldElement[] uShares = field.createShares(u, NPLAYERS);
        // the macs that allow the u to be reliably opened
        FieldElement[][] uMACs = new FieldElement[NPLAYERS][NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            FieldElement mac = betas[i].mult(u);
            FieldElement[] macShares = field.createShares(mac, NPLAYERS);
            for (int j = 0; j < NPLAYERS; j++) {
                uMACs[i][j] = macShares[j];
            }
        }

        // the u_s
        ExtendedRepresentation[] u_i_s = new ExtendedRepresentation[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            Map<String, FieldElement> playerToMAC = new HashMap();
            for (int j = 0; j < NPLAYERS; j++) {
                playerToMAC.put("localhost:" + (PORT + j), uMACs[j][i]);
            }
            ExtendedRepresentation u_i = new ExtendedRepresentation(betas[i], uShares[i], playerToMAC);
            u_i_s[i] = u_i;
        }

        // create an extended representation to be used in the commit of each player
        ExtendedRepresentationWithSum[] myCommit = new ExtendedRepresentationWithSum[NPLAYERS];
        Map<String, ExtendedRepresentation>[] theirCommit = new Map[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            theirCommit[i] = new HashMap();
        }
        for (int p = 0; p < NPLAYERS; p++) {
            FieldElement[] betasAgain = new FieldElement[NPLAYERS];
            for (int i = 0; i < NPLAYERS; i++) {
                betasAgain[i] = field.random(clazz);
            }

            FieldElement s = field.random(clazz);
            FieldElement[] sShares = field.createShares(s, NPLAYERS);
            FieldElement[][] sMACs = new FieldElement[NPLAYERS][NPLAYERS];
            for (int i = 0; i < NPLAYERS; i++) {
                FieldElement mac = betasAgain[i].mult(s);
                FieldElement[] macShares = field.createShares(mac, NPLAYERS);
                for (int j = 0; j < NPLAYERS; j++) {
                    sMACs[i][j] = macShares[j];
                }
            }

            for (int i = 0; i < NPLAYERS; i++) {
                Map<String, FieldElement> playerToMAC = new HashMap();
                for (int j = 0; j < NPLAYERS; j++) {
                    playerToMAC.put("localhost:" + (PORT + j), sMACs[j][i]);
                }
                if (i == p) {
                    myCommit[i] = new ExtendedRepresentationWithSum(betasAgain[i], sShares[i], s, playerToMAC);
                } else {
                    ExtendedRepresentation s_i = new ExtendedRepresentation(betasAgain[i], sShares[i], playerToMAC);
                    theirCommit[i].put("localhost" + (PORT + p), s_i);
                }
            }

        }

        // create the batch check values for all players
        BatchCheckValues[] batchCheckValues = new BatchCheckValues[NPLAYERS];
        for (int i = 0; i < NPLAYERS; i++) {
            batchCheckValues[i] = new BatchCheckValues(u_i_s[i], myCommit[i], theirCommit[i]);
        }

        Player[] players = new Player[NPLAYERS];
        ArrayList<PlayerInfo> playersID = new ArrayList();
        for (int i = 0; i < NPLAYERS; i++) {
            players[i] = new Player(i, "localhost", PORT + i);
            playersID.add(players[i].getInfo());
        }

        for (int i = 0; i < NPLAYERS; i++) {
            players[i].setCircuit(circuit);
            players[i].setMOD(MOD);

            players[i].setInputs(inputShares[i].get());
            players[i].setBeaverTriples(beaverTriples[i]);

            players[i].setBatchCheckValues(batchCheckValues[i]);

            ArrayList<PlayerInfo> playersIDCopy = new ArrayList(playersID);
            playersIDCopy.remove(players[i].getInfo());
            players[i].setPlayers(playersIDCopy);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < NPLAYERS; i++) {
            players[i].start();
        }
        for (int i = 0; i < NPLAYERS; i++) {
            players[i].join();
        }

        System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - start));
    }
}
