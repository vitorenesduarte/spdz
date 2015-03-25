package sdc.spdz.circuits.player;

import java.util.UUID;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class PlayerID {

   private final String UID;
   private final String host;
   private final int port;

   public PlayerID(String host, int port) {
      this.UID = UUID.randomUUID().toString();
      this.host = host;
      this.port = port;
   }

   public String getUID() {
      return UID;
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
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

      PlayerID playerID = (PlayerID) o;

      return UID.equals(playerID.getUID());
   }

}
