package sdc.avoidingproblems.circuits.algebra;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 * @author Paulo Silva
 */
public class Field {

   private static final Logger logger = Logger.getLogger(Field.class.getName());

   private final int MOD;
   private final SecureRandom random;

   public Field(int MOD) {
      this.MOD = MOD;
      this.random = new SecureRandom();
   }

   public FieldElement random(Class<?> clazz) throws ClassNotSupportedException {
      int value = random.nextInt(MOD);
      FieldElement result = Util.getFieldElementInstance(clazz, value, MOD);
      return result;
   }

   // TO-DO : verify this
   public ValueAndMAC[] createShares(ValueAndMAC vam, int NSHARES) {
      FieldElement[] valueShares = createShares(vam.getValue(), NSHARES);
      FieldElement[] MACShares = createShares(vam.getMAC(), NSHARES);

      ValueAndMAC[] shares = new ValueAndMAC[NSHARES];
      for (int i = 0; i < NSHARES; i++) {
         shares[i] = new ValueAndMAC(valueShares[i], MACShares[i]);
      }

      return shares;
   }

   private FieldElement[] createShares(FieldElement x, int NSHARES) {
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

      ValueAndMAC aAndMAC = new ValueAndMAC(a, aMAC);
      ValueAndMAC bAndMAC = new ValueAndMAC(b, bMAC);
      ValueAndMAC cAndMAC = new ValueAndMAC(c, cMAC);

      BeaverTriple triple = new BeaverTriple(aAndMAC, bAndMAC, cAndMAC);
      return triple;
   }
}
