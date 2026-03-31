import javax.swing.*;
import java.awt.*;
public abstract class Entity extends JLabel {
    int posX;
    int posY;

    int originX;
    int originY;

    public Entity(int x, int y){
        posX = x;
        originX=x;
        posY = y;
        originY=y;
    }
}
