
package spaceshootingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRecord {
    // A list to record the move of all the players
    private List<Integer> gameRecord = Collections.synchronizedList(new ArrayList<>()); 
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
    
    public void addMove(int playerId, int move){
        gameRecord.add(playerId);
        gameRecord.add(move);
    }
    
    public void getMove(){
    }                
    
    public int getMoveCount() { return gameRecord.size(); }        
}

