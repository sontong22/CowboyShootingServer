
package spaceshootingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRoom {
    private List<String> gameRoom = Collections.synchronizedList(new ArrayList<String>());
    private String roomName;
    private int numOfPlayer = 0;      
    
    public GameRoom(){        
    }
    
    public GameRoom(String name) {
        this.roomName = name;
    }
    
    public void playerEntered(){ numOfPlayer++;}    
    public void playerExited(){ numOfPlayer--;}    
    public int getNumOfPlayer(){ return numOfPlayer;}
    public String getRoomName() { return roomName;}
    
    public void getMove(){
    }
    
    
    public int getSize() { return gameRoom.size(); }
    public String getComment(int n) { return gameRoom.get(n); }
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

