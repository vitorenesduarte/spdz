package sdc.avoidingproblems.player;

import sdc.avoidingproblems.ArgumentUtil;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerMain {

    public static void main(String[] args) {
        try {
            Integer port = null;
            for (String arg : args) {
                if (arg.startsWith("--port=")) {
                    port = ArgumentUtil.getValueAsInteger(arg);
                }
            }

            if (port == null) {
                System.err.println("Arguments missing:");
                System.err.println("--port=<port where this player will be listening>");
            }

            Player player = new Player("localhost", port);
            player.start();
            player.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
