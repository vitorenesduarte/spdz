package sdc.avoidingproblems.circuits.player;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerInfo {

    private final Integer UID;
    private final String host;
    private final Integer port;

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
