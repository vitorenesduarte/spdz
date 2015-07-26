package sdc.avoidingproblems.player;

import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    private final Semaphore PLAYER_END;
    private final Semaphore ACCEPTOR_END;

    public Acceptor(Integer port, Inbox inbox, Semaphore dealerIsDone, DealerData dealerData, Semaphore PLAYER_END, Semaphore ACCEPTOR_END) {
        this.PORT = port;
        this.inbox = inbox;
        this.dealerIsDone = dealerIsDone;
        this.dealerData = dealerData;
        this.PLAYER_END = PLAYER_END;
        this.ACCEPTOR_END = ACCEPTOR_END;
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(PORT)) {
            receiveDealerMessageAndGO(ss.accept());
            dealerIsDone.release();

            inbox.setNumberOfOtherPlayers(dealerData.getOtherPlayers().size());

            List<Socket> sockets = new ArrayList<>();
            for (int i = 0; i < inbox.getNumberOfOtherPlayers(); i++) {
                Socket socket = ss.accept();
                sockets.add(socket);
                //socket.setSoTimeout(TIMEOUT);
                new InboxReader(socket, inbox).start();
            }
            logger.info("all players accepted");

            PLAYER_END.acquire();
            for (Socket socket : sockets) {
                socket.close();
            }
            ACCEPTOR_END.release();
        } catch (IOException | InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void receiveDealerMessageAndGO(Socket socket) throws IOException {
        try (InputStreamReader in = new InputStreamReader(socket.getInputStream(), "UTF-8");
                JsonReader reader = new JsonReader(in)) {

            DealerData data = JSONManager.fromJSON(reader, DealerData.class);
            dealerData.setAll(data);

            BufferedReader reader_ = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            LineIterator iterator = new LineIterator(reader_);
            if (iterator.hasNext()) {
                String status = iterator.next();
                if (!status.equals("GO")) {
                    logger.log(Level.SEVERE, "there is something wrong - status : {0}", status);
                } else{
                    logger.info("GO received from dealer");
                }
            }
        } finally {
            socket.close();
        }
    }
}
