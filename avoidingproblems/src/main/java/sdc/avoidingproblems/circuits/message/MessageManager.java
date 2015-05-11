package sdc.avoidingproblems.circuits.message;

import sdc.avoidingproblems.circuits.JSONManager;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class MessageManager {

   private static final String SEP = "::";

   public static Object getMessage(String message) throws ClassNotFoundException {
      int i = message.indexOf(SEP);
      String className = message.substring(0, i);
      String json = message.substring(i + SEP.length(), message.length() - 1);

      return JSONManager.fromJSON(json, Class.forName(className));
   }

   public static String createMessage(Object o) {
      return o.getClass().getName() + SEP + JSONManager.toJSON(o) + "\n";
   }

   public static void main(String[] args) throws ClassNotFoundException {
      MultiplicationShare m1 = new MultiplicationShare(3L, 10L, 20L);
      String message = createMessage(m1);

      MultiplicationShare m2 = (MultiplicationShare) getMessage(message);
      System.out.println(m2);
   }
}
