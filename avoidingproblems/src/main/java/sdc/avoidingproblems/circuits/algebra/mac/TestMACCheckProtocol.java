package sdc.avoidingproblems.circuits.algebra.mac;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sdc.avoidingproblems.circuits.ExecutionMode.DISTRIBUTED;
import sdc.avoidingproblems.circuits.GateSemantic;
import static sdc.avoidingproblems.circuits.GateSemantic.MULT;
import sdc.avoidingproblems.circuits.algebra.BeaverTriple;
import sdc.avoidingproblems.circuits.algebra.BigIntegerFE;
import sdc.avoidingproblems.circuits.algebra.Field;
import sdc.avoidingproblems.circuits.algebra.FieldElement;
import sdc.avoidingproblems.circuits.algebra.Function;
import sdc.avoidingproblems.circuits.algebra.Util;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;
import sdc.avoidingproblems.circuits.exception.ExecutionModeNotSupportedException;
import sdc.avoidingproblems.circuits.exception.InvalidParamException;

/**
 *
 * @author Vitor Enes
 */
public class TestMACCheckProtocol {

    private static final Logger logger = Logger.getLogger(TestMACCheckProtocol.class.getName());

    public static void main(String[] args) {
        try {
            Long MOD = 41L;
            int NPLAYERS = 2;
            boolean imSupposedToLie = new SecureRandom().nextBoolean();
            if (imSupposedToLie) {
                System.out.println("P0 says: I will lie (►_◄)");
            }

            Field field = new Field(MOD);
            Class<?> clazz = BigIntegerFE.class;
            FieldElement fixedMACKey = field.random(clazz);

            FieldElement[] alphas = field.createShares(fixedMACKey, NPLAYERS);
            FieldElement alphaP0 = alphas[0];
            FieldElement alphaP1 = alphas[1];

            // circuit --> (x . y) . z
            FieldElement xFE = field.random(clazz);
            FieldElement yFE = field.random(clazz);
            FieldElement zFE = field.random(clazz);

            SimpleRepresentation xMAC = new SimpleRepresentation(xFE, xFE.mult(fixedMACKey));
            SimpleRepresentation yMAC = new SimpleRepresentation(yFE, yFE.mult(fixedMACKey));
            SimpleRepresentation zMAC = new SimpleRepresentation(zFE, zFE.mult(fixedMACKey));

            System.out.println("MOD : " + MOD);
            System.out.println("{x : " + xMAC.getValue().longValue() + ", y : " + yMAC.getValue().longValue() + ", z : " + zMAC.getValue().longValue() + "}");
            System.out.println("{xy : " + (xMAC.getValue().longValue() * yMAC.getValue().longValue()) % MOD + "}");
            System.out.println("{xyz : " + (xMAC.getValue().longValue() * yMAC.getValue().longValue() * zMAC.getValue().longValue()) % MOD + "}");

            BeaverTriple fstTriple = field.randomMultiplicationTriple(clazz, fixedMACKey);
            BeaverTriple sndTriple = field.randomMultiplicationTriple(clazz, fixedMACKey);

            // create shares
            SimpleRepresentation[] xMACShares = field.createShares(xMAC, NPLAYERS);
            SimpleRepresentation[] yMACShares = field.createShares(yMAC, NPLAYERS);
            SimpleRepresentation[] zMACShares = field.createShares(zMAC, NPLAYERS);

            SimpleRepresentation[] aFstTripleShares = field.createShares(fstTriple.getA(), NPLAYERS);
            SimpleRepresentation[] bFstTripleShares = field.createShares(fstTriple.getB(), NPLAYERS);
            SimpleRepresentation[] cFstTripleShares = field.createShares(fstTriple.getC(), NPLAYERS);

            SimpleRepresentation[] aSndTripleShares = field.createShares(sndTriple.getA(), NPLAYERS);
            SimpleRepresentation[] bSndTripleShares = field.createShares(sndTriple.getB(), NPLAYERS);
            SimpleRepresentation[] cSndTripleShares = field.createShares(sndTriple.getC(), NPLAYERS);

            BeaverTriple fstTripleP0 = new BeaverTriple(aFstTripleShares[0], bFstTripleShares[0], cFstTripleShares[0]);
            BeaverTriple fstTripleP1 = new BeaverTriple(aFstTripleShares[1], bFstTripleShares[1], cFstTripleShares[1]);

            BeaverTriple sndTripleP0 = new BeaverTriple(aSndTripleShares[0], bSndTripleShares[0], cSndTripleShares[0]);
            BeaverTriple sndTripleP1 = new BeaverTriple(aSndTripleShares[1], bSndTripleShares[1], cSndTripleShares[1]);

            // Player 0
            SimpleRepresentation dFstP0 = xMACShares[0].sub(fstTripleP0.getA());
            SimpleRepresentation eFstP0 = yMACShares[0].sub(fstTripleP0.getB());

            // Player 1
            SimpleRepresentation dFstP1 = xMACShares[1].sub(fstTripleP1.getA());
            SimpleRepresentation eFstP1 = yMACShares[1].sub(fstTripleP1.getB());

            // Both
            FieldElement dFst = Util.getFieldElementInstance(clazz,
                    dFstP0.getValue().longValue() + dFstP1.getValue().longValue(),
                    MOD);

            FieldElement eFst = Util.getFieldElementInstance(clazz,
                    eFstP0.getValue().longValue() + eFstP1.getValue().longValue(),
                    MOD);

            Function f = GateSemantic.getFunction(MULT);
            // Player 0
            SimpleRepresentation xyP0 = f.apply(DISTRIBUTED, fstTripleP0, dFst, eFst, dFstP0);

            // Player 1
            SimpleRepresentation xyP1 = f.apply(DISTRIBUTED, fstTripleP1, dFst, eFst, dFstP1);

            System.out.println("{xy0 : " + xyP0.getValue().longValue() + ", xy1 : " + xyP1.getValue().longValue() + "}");
            System.out.println("{xy : " + (xyP0.getValue().longValue() + xyP1.getValue().longValue()) % MOD + "}");

            // Player 0
            SimpleRepresentation dSndP0 = imSupposedToLie
                    ? new SimpleRepresentation(field.random(clazz), xyP0.sub(sndTripleP0.getA()).getMAC())
                    : xyP0.sub(sndTripleP0.getA());
            SimpleRepresentation eSndP0 = zMACShares[0].sub(sndTripleP0.getB());

            // Player 1
            SimpleRepresentation dSndP1 = xyP1.sub(sndTripleP1.getA());
            SimpleRepresentation eSndP1 = zMACShares[1].sub(sndTripleP1.getB());

            // Both
            FieldElement dSnd = Util.getFieldElementInstance(clazz,
                    dSndP0.getValue().longValue() + dSndP1.getValue().longValue(),
                    MOD);

            FieldElement eSnd = Util.getFieldElementInstance(clazz,
                    eSndP0.getValue().longValue() + eSndP1.getValue().longValue(),
                    MOD);

            // Player 0
            SimpleRepresentation xyzP0 = f.apply(DISTRIBUTED, sndTripleP0, dSnd, eSnd, dSndP0);

            // Player 1
            SimpleRepresentation xyzP1 = f.apply(DISTRIBUTED, sndTripleP1, dSnd, eSnd, dSndP1);

            System.out.println("{xyz0 : " + xyzP0.getValue().longValue() + ", xyz1 : " + xyzP1.getValue().longValue() + "}");
            System.out.println("{xyz : " + (xyzP0.getValue().longValue() + xyzP1.getValue().longValue()) % MOD + "}");

            // let's mac check now
            FieldElement u = field.random(clazz);
            /*
            
             // we won't need this because we're skiping:
             - opens
             - commits
             FieldElement betaP0 = field.random(clazz);
             FieldElement betaP1 = field.random(clazz);

             FieldElement r = field.random(clazz);
             FieldElement[] rShares = field.createShares(r, NPLAYERS);
             FieldElement rMAC0 = betaP0.mult(r);
             FieldElement rMAC1 = betaP1.mult(r);
             FieldElement[] rMAC0Shares = field.createShares(rMAC0, NPLAYERS);
             FieldElement[] rMAC1Shares = field.createShares(rMAC1, NPLAYERS);

             FieldElement s = field.random(clazz);
             FieldElement[] sShares = field.createShares(s, NPLAYERS);
             FieldElement sMAC0 = betaP0.mult(s);
             FieldElement sMAC1 = betaP1.mult(s);
             FieldElement[] sMAC0Shares = field.createShares(sMAC0, NPLAYERS);
             FieldElement[] sMAC1Shares = field.createShares(sMAC1, NPLAYERS);
            
             FieldElement[] uShares = field.createShares(u, NPLAYERS);
             FieldElement uMAC0 = betaP0.mult(u);
             FieldElement uMAC1 = betaP1.mult(u);
             FieldElement[] uMAC0Shares = field.createShares(rMAC0, NPLAYERS);
             FieldElement[] uMAC1Shares = field.createShares(rMAC1, NPLAYERS);
             */

            // opened values so far:
            // dFst, eFst, dSnd, eSnd
            // each player has:
            // [dFst], [eFst], [dSnd], [eSnd] and dFst', eFst', dSnd', eSnd' and want to check the opened values
            // each ei will be u^i, so we need to open u first:
            // run open(u)
            // P0 sends uShares[0] to P1
            // P1 sends uShares[1] to P0
            // ...
            // both will check and now both know the value of u
            // P0
            SimpleRepresentation yP0 = dFstP0.mult(u.pow(0L))
                    .add(eFstP0.mult(u.pow(1L)))
                    .add(dSndP0.mult(u.pow(2L)))
                    .add(eSndP0.mult(u.pow(3L)));

            // P1
            SimpleRepresentation yP1 = dFstP1.mult(u.pow(0L))
                    .add(eFstP1.mult(u.pow(1L)))
                    .add(dSndP1.mult(u.pow(2L)))
                    .add(eSndP1.mult(u.pow(3L)));

            // BOTH
            FieldElement y_ = dFst.mult(u.pow(0L))
                    .add(eFst.mult(u.pow(1L)))
                    .add(dSnd.mult(u.pow(2L)))
                    .add(eSnd.mult(u.pow(3L)));

            // P0
            FieldElement d0 = alphaP0.mult(y_).sub(yP0.getMAC());

            // P1
            FieldElement d1 = alphaP1.mult(y_).sub(yP1.getMAC());

            // skiping commit(d0) and commit(d1)
            if (d0.add(d1).longValue() == 0) {
                System.out.println("ACCEPT");
            } else {
                System.out.println("REFUSE");
            }

        } catch (ClassNotSupportedException | InvalidParamException | ExecutionModeNotSupportedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
