package sdc.avoidingproblems.player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Acceptor extends Thread {

    private static final Logger logger = Logger.getLogger(Acceptor.class.getName());
    private static final int TIMEOUT = 1 * 60 * 1000; // 1 minute

    private final Integer PORT;
    private final Inbox inbox;

    public Acceptor(Integer port, Inbox inbox) {
        this.PORT = port;
        this.inbox = inbox;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            for (int i = 0; i < inbox.getNumberOfOtherPlayers(); i++) {
                Socket socket = ss.accept();
                socket.setSoTimeout(TIMEOUT);
                new InboxReader(socket, inbox).start();
            }
        logger.info("all players accepted");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
