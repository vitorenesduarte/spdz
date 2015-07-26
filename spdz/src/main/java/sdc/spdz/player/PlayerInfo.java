package sdc.spdz.player;

import java.io.PrintWriter;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerInfo {

    private final String host;
    private final Integer port;
    private final String hostAndPort;

    public PlayerInfo(String host, int port) {
        this.host = host;
        this.port = port;
        this.hostAndPort = host + ":" + port;
    }

    public PlayerInfo(String hostAndPort) {
        String[] parts = hostAndPort.split(":");
        this.host = parts[0];
        this.port = Integer.parseInt(parts[1]);
        this.hostAndPort = hostAndPort;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getHostAndPort() {
        return hostAndPort;
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

        return hostAndPort.equals(playerID.getHostAndPort());
    }

    @Override
    public String toString() {
        return "PlayerInfo{host=" + host + ", port=" + port + '}';
    }
}
