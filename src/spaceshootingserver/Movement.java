
package spaceshootingserver;

public class Movement {
    // type = 1: movement of cowboy
    // type = 2: movement of missile
    public int type;
    public int playerId;
    public int x;
    public int y;
    
    public Movement(int type, int id, int x, int y){
        this.type = type;
        playerId = id;
        this.x = x;
        this.y = y;
    }
    
    public String toString(){
        return type+ " " + playerId + " " + x + " " + y;
    }
}
