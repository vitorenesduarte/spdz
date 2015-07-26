package sdc.avoidingproblems.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.LineIterator;
import sdc.avoidingproblems.message.Message;
import sdc.avoidingproblems.message.MessageManager;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class InboxReader extends Thread {

    private static final Logger logger = Logger.getLogger(InboxReader.class.getName());

    private final Socket socket;
    private final Inbox inbox;

    public InboxReader(Socket socket, Inbox inbox) {
        this.socket = socket;
        this.inbox = inbox;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            final LineIterator iterator = new LineIterator(in);
            while (iterator.hasNext()) {
                Message message = MessageManager.getMessage(iterator.next());
                inbox.addMessage(message);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            // swalling socket close exception because we're doing this on purpose
            // TODO find better way to terminate the inbox readers
        }
    }
}
