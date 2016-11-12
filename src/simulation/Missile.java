package simulation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import physics.*;

public class Missile {
    private Ray r;
    private Rectangle missile;
    
    public Missile(int startX,int startY,int dX,int dY)
    {
        Vector v = new Vector(dX,dY);
        double speed = v.length();
        r = new Ray(new Point(startX,startY),v,speed);
    }
    
    public Ray getRay()
    {
        return r;
    }
    
    public void setRay(Ray r)
    {
        this.r = r;
    }
    
    public void move(double time)
    {
        r = new Ray(r.endPoint(time),r.v,r.speed);
    }
    
    public Shape getShape()
    {
        missile = new Rectangle(r.origin.x,r.origin.y,2,5);
        missile.setFill(Color.RED);
        return missile;
    }
    
    public void updateShape()
    {
        missile.setX(r.origin.x);
        missile.setY(r.origin.y);
    }
}
