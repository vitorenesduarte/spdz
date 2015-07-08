package sdc.avoidingproblems.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerInfo {

    private final Integer UID;
    private final String host;
    private final Integer port;
    private PrintWriter writer;

    public PlayerInfo(Integer UID, String host, Integer port) {
        this.UID = UID;
        this.host = host;
        this.port = port;
    }

    public Integer getUID() {
        return UID;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getHostAndPort() {
        return host + ":" + port;
    }

    public void setSocket(Socket socket) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message){
        this.writer.println(message);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayerInfo playerID = (PlayerInfo) o;

        return UID.equals(playerID.getUID());
    }

    @Override
    public String toString() {
        return "PlayerInfo{" + "UID=" + UID + ", host=" + host + ", port=" + port + '}';
    }
}
