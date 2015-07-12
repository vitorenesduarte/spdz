package sdc.avoidingproblems.player;

import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.LineIterator;
import sdc.avoidingproblems.JSONManager;
import sdc.avoidingproblems.message.DealerData;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Acceptor extends Thread {

    private static final Logger logger = Logger.getLogger(Acceptor.class.getName());
    //private static final int TIMEOUT = 5 * 60 * 1000; // 1 minute

    private final Integer PORT;
    private final Inbox inbox;
    private final Semaphore dealerIsDone;
    private final DealerData dealerData;

    public Acceptor(Integer port, Inbox inbox, Semaphore dealerIsDone, DealerData dealerData) {
        this.PORT = port;
        this.inbox = inbox;
        this.dealerIsDone = dealerIsDone;
        this.dealerData = dealerData;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            receiveDealerMessage(ss.accept());
            receiveDealerGO(ss.accept());
            dealerIsDone.release();

            inbox.setNumberOfOtherPlayers(dealerData.getOtherPlayers().size());

            for (int i = 0; i < inbox.getNumberOfOtherPlayers(); i++) {
                Socket socket = ss.accept();
                //socket.setSoTimeout(TIMEOUT);
                new InboxReader(socket, inbox).start();
            }
            logger.info("all players accepted");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void receiveDealerMessage(Socket socket) throws IOException {
        try (InputStreamReader in = new InputStreamReader(socket.getInputStream(), "UTF-8");
                JsonReader reader = new JsonReader(in)) {

            DealerData data = JSONManager.fromJSON(reader, DealerData.class);
            dealerData.setAll(data);

        } finally {
            socket.close();
        }
    }

    private void receiveDealerGO(Socket socket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String status = reader.readLine();
            if (!status.equals("GO")) {
                logger.severe("there is something wrong");
            }
        } finally {
            socket.close();
        }
    }
}
