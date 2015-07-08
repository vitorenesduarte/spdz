package sdc.avoidingproblems.algebra;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import sdc.avoidingproblems.exception.ClassNotSupportedException;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Util {

    public static FieldElement getFieldElementInstance(Class<?> clazz, BigInteger value, BigInteger MOD) throws ClassNotSupportedException {
        try {
            Constructor<?> constructor = clazz.getConstructor(BigInteger.class, BigInteger.class);
            FieldElement result = (FieldElement) constructor.newInstance(value, MOD);
            return result;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ClassNotSupportedException(ex.getMessage());
            // maybe we're swallowing too many exceptions
        }
    }
}
