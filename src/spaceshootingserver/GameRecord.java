
package spaceshootingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRecord {
    // A list to record the movement of cowboys
    private List<Movement> cowboyRecord = Collections.synchronizedList(new ArrayList<>()); 
    
    // A list to record the movement of missiles
    private List<Movement> missileRecord = Collections.synchronizedList(new ArrayList<>()); 
    
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
    
    public void addCowboyMove(Movement move){ cowboyRecord.add(move);}
    public void addMissileMove(Movement move){ missileRecord.add(move);}
    
    public Movement getCowboyMove(int n){ return cowboyRecord.get(n); }                
    public Movement getMissileMove(int n){ return missileRecord.get(n); }                
    
    public int getCowboyMoveCount() { return cowboyRecord.size(); }        
    public int getMissileMoveCount() { return missileRecord.size(); }        
}

