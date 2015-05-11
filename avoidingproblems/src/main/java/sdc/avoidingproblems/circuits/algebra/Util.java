package sdc.avoidingproblems.circuits.algebra;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sdc.avoidingproblems.circuits.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Util {

   public static FieldElement getFieldElementInstance(Class<?> clazz, Long value, Long MOD) throws ClassNotSupportedException {
      try {
         Constructor<?> constructor = clazz.getConstructor(Integer.class, Integer.class);
         FieldElement result = (FieldElement) constructor.newInstance(value, MOD);
         return result;
      } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
         throw new ClassNotSupportedException(ex.getMessage());
         // maybe we're swallowing too many exceptions
      }
   }

}
