
package spaceshootingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRecord {
    // A list to record the movement of objects
    private List<Movement> movementRecord = Collections.synchronizedList(new ArrayList<>()); 
    
    // A list to record the player currently in this room
    private List<Player> playerList = Collections.synchronizedList(new ArrayList<>()); 
    
    public GameRecord(){        
    }
    
    public void addPlayer(Player player){
        playerList.add(player);
    }
    
    public boolean getState(){
        if(playerList.size() == 2){
            return true;
        } else{
            return false;
        }
    }
    
    public int getPlayerId(int n){
        return playerList.get(n).getPlayerId();
    }
    
    public void addMove(Movement move){ movementRecord.add(move);}    
    public Movement getMove(int n){ return movementRecord.get(n); }                      
    public int getMoveCount() { return movementRecord.size(); }          
}

