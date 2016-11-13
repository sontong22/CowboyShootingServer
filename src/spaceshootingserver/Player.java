
package spaceshootingserver;

public class Player {
    private String playerName;
    private int playerId;    
    
    public Player(){        
    }
    
    public Player(int id){        
        playerId = id;
    }
    
    public void setPlayerName(String name){
        playerName = name;
    }
    
    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String toString() { return playerName + " (" + playerId + ")";}
}
