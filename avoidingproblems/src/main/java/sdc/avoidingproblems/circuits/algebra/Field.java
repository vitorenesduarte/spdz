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
      try {
         Constructor<?> constructor = clazz.getConstructor(Integer.class, Integer.class);
         FieldElement result = (FieldElement) constructor.newInstance(value, MOD);
         return result;
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
         throw new ClassNotSupportedException(ex.getMessage());
         // maybe we're swallowing too many exceptions
      }
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

   public BeaverTriple randomMultiplicationTriple(Class<?> clazz) throws ClassNotSupportedException {
      FieldElement a = random(clazz);
      FieldElement b = random(clazz);
      FieldElement c = new BigIntegerFE(a.intValue(), MOD).mult(b);

      BeaverTriple triple = new BeaverTriple(a, b, c);
      return triple;
   }
}
