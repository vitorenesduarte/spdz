package sdc.avoidingproblems.algebra;

import java.math.BigInteger;
import sdc.avoidingproblems.algebra.mac.SimpleRepresentation;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Field {

    private static final Logger logger = Logger.getLogger(Field.class.getName());

    private final BigInteger MOD;
    private final SecureRandom random;

    public Field(BigInteger MOD) {
        this.MOD = MOD;
        this.random = new SecureRandom();
    }

    public FieldElement random(Class<?> clazz) throws ClassNotSupportedException {
        BigInteger value = BigInteger.ZERO;
        while (value.compareTo(BigInteger.ZERO) <= 0 || value.compareTo(MOD) >= 0) { // does this compromises anything?
            value = new BigInteger(MOD.bitCount(), random);
        }
        FieldElement result = Util.getFieldElementInstance(clazz, value, MOD);
        return result;
    }

    public SimpleRepresentation[] createShares(SimpleRepresentation vam, int NSHARES) {
        FieldElement[] valueShares = createShares(vam.getValue(), NSHARES);
        FieldElement[] MACShares = createShares(vam.getMAC(), NSHARES);

        SimpleRepresentation[] shares = new SimpleRepresentation[NSHARES];
        for (int i = 0; i < NSHARES; i++) {
            shares[i] = new SimpleRepresentation(valueShares[i], MACShares[i]);
        }

        return shares;
    }

    public FieldElement[] createShares(FieldElement x, int NSHARES) {
        FieldElement[] shares = new FieldElement[NSHARES];
        shares[NSHARES - 1] = x;
        for (int i = 0; i < NSHARES - 1; i++) {
            try {
                
                shares[i] = random(x.getClass());
                shares[NSHARES - 1] = shares[NSHARES - 1].sub(shares[i]);
            } catch (ClassNotSupportedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return shares;
    }

    public BeaverTriple randomMultiplicationTriple(Class<?> clazz, FieldElement fixedMACKey) throws ClassNotSupportedException {

        FieldElement a = random(clazz);
        FieldElement aMAC = a.mult(fixedMACKey);
        FieldElement b = random(clazz);
        FieldElement bMAC = b.mult(fixedMACKey);
        FieldElement c = a.mult(b);
        FieldElement cMAC = c.mult(fixedMACKey);

        SimpleRepresentation aAndMAC = new SimpleRepresentation(a, aMAC);
        SimpleRepresentation bAndMAC = new SimpleRepresentation(b, bMAC);
        SimpleRepresentation cAndMAC = new SimpleRepresentation(c, cMAC);

        BeaverTriple triple = new BeaverTriple(aAndMAC, bAndMAC, cAndMAC);
        return triple;
    }
}
