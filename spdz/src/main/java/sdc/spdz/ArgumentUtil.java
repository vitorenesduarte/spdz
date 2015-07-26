package sdc.spdz;

import java.io.File;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class ArgumentUtil {

    private static final String ARRAY_SEPARATOR = ",";

    public static String getValueAsString(String argument) {
        return argument.substring(argument.indexOf("=") + 1);
    }

    public static String[] getValueAsArray(String argument) {
        return getValueAsString(argument).split(ARRAY_SEPARATOR);
    }

    public static File getValueAsFile(String argument) {
        return new File(getValueAsString(argument));
    }

    public static Boolean getValueAsBoolean(String arg) {
        return Boolean.parseBoolean(getValueAsString(arg));
    }

    public static int getValueAsInteger(String arg) {
        return Integer.parseInt(getValueAsString(arg));
    }
}
