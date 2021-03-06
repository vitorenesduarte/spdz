package sdc.spdz.message;

import sdc.spdz.JSONManager;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class MessageManager {

    private static final String SEP = "::";

    public static Message getMessage(String message) throws ClassNotFoundException {
        int i = message.indexOf(SEP);
        String className = message.substring(0, i);
        String json = message.substring(i + SEP.length());

        return (Message) JSONManager.fromJSON(json, Class.forName(className));
    }

    public static String createMessage(Message message) {
        return message.getClass().getName() + SEP + JSONManager.toJSON(message);
    }
}
