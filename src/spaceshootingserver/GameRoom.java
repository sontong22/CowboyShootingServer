
package spaceshootingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRoom {
    // A list to record the move of all the players
    private List<Integer> gameRecord = Collections.synchronizedList(new ArrayList<>()); 
    // A list to remember the list of player currently in a room
    private List<String> playerList = Collections.synchronizedList(new ArrayList<>()); 
    
    private String roomName;
    private int numOfPlayer = 0;   
    
    private boolean isGameStarted = false;
    
    public GameRoom(){        
    }
    
    public GameRoom(String name) {
        this.roomName = name;
    }
    
    public List<String> getPlayerList(){
        return playerList;
    }
    
    public void playerEntered(){ numOfPlayer++;}    
    public void playerExited(){ numOfPlayer--;}    
    public int getNumOfPlayer(){ return numOfPlayer;}
    public String getRoomName() { return roomName;}
    
    public void startGame(){
        isGameStarted = true;
    }
    
    public void endGame(){
        isGameStarted = false;
    }
    
    public boolean getGameState(){
        return isGameStarted;
    }
    
    public void addMove(int playerId, int move){
        gameRecord.add(playerId);
        gameRecord.add(move);
    }
    
    public void getMove(){
    }                
    public int getMoveCount() { return gameRecord.size(); }    
    public String toString() { return roomName;}

    //Run a thread
    public void waitForNoClients() {
        while (numOfPlayer != 0) {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException ex) {
                System.err.println("Error in waitForNoClients");
                ex.printStackTrace();
            }
        }
    }
}

