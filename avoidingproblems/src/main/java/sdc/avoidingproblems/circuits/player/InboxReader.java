package sdc.avoidingproblems.circuits.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdc.avoidingproblems.circuits.message.Message;
import sdc.avoidingproblems.circuits.message.MessageManager;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class InboxReader extends Thread {

    private static final Logger logger = Logger.getLogger(InboxReader.class.getName());
    
    private final Integer PORT;
    private final Inbox inbox;
    
    public InboxReader(Integer port, Inbox inbox) {
        this.PORT = port;
        this.inbox = inbox;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                try (
                        Socket socket = ss.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    
                    String line = in.readLine();
                    Message message = MessageManager.getMessage(line);
                    inbox.addMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
