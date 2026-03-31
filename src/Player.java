import javax.swing.*;
import java.awt.*;
public class Player extends Entity {
    ImageIcon[] currSpriteSheet;
    ImageIcon[] spritesLeft;
    ImageIcon[] spritesRight;
    ImageIcon[] spritesUp;
    ImageIcon[] spritesDown;
    int currSpriteNum;
    ImageIcon currSprite;

    boolean invincible;
    boolean ghostmode;

    public Player(int x, int y){
        super(x,y);
        ghostmode=false;
        invincible=false;
        setHorizontalAlignment(CENTER);

        spritesLeft = loadSprites("l");
        spritesRight = loadSprites("r");
        spritesUp = loadSprites("u");
        spritesDown = loadSprites("d");
        currSpriteSheet=spritesLeft;
        currSpriteNum=0;
        currSprite = currSpriteSheet[currSpriteNum];

        setBackground(Color.BLACK);
        setIcon(currSprite);
        setBackground(Color.BLACK);
        System.out.println(this.getX()+" "+this.getY());
    }

    public void move(moveDirection dir,Map map, char[] entitymap){
        //System.out.println(ghostmode);
        switch(dir){
            case UP:
                if(map.getTile(posX,posY-1)!='2') {
                    if((map.getTile(posX,posY-1)=='1' && ghostmode) || map.getTile(posX,posY-1)!='1'){
                        entitymap[posX+posY*map.width]='.';
                        posY--;
                        currSpriteSheet=spritesUp;
                        entitymap[posX+posY*map.width]='P';
                    }
                }
                break;
            case DOWN:
                if(map.getTile(posX,posY+1)!='2'){
                    if((map.getTile(posX,posY+1)=='1' && ghostmode) || map.getTile(posX,posY+1)!='1'){
                        entitymap[posX+posY*map.width]='.';
                        posY++;
                        currSpriteSheet=spritesDown;
                        entitymap[posX+posY*map.width]='P';
                    }
                }
                break;
            case LEFT:
                if(posX!=0 && map.getTile(posX-1,posY)!='2'){
                    if(map.getTile(posX-1,posY)!='1' || (map.getTile(posX-1,posY)=='1' && ghostmode)){
                        entitymap[posX+posY*map.width]='.';
                        posX--;
                        currSpriteSheet=spritesLeft;
                        entitymap[posX+posY*map.width]='P';
                    }
                }
                break;
            case RIGHT:
                if(posX!=map.width-1 && map.getTile(posX+1,posY)!='2'){
                    if(map.getTile(posX+1,posY)!='1' || (map.getTile(posX+1,posY)=='1' && ghostmode)){
                        entitymap[posX+posY*map.width]='.';
                        posX++;
                        currSpriteSheet=spritesRight;
                        entitymap[posX+posY*map.width]='P';
                    }
                }
                break;
            default:
                break;

        }
        advanceSprite(); //ADVANCES TO NEXT ANIMATION FRAME IN SEQUENCE
        setIcon(currSprite); //SETS CURRENT ANIMATION FRAME
    }

    public ImageIcon[] loadSprites(String suffix){
        ImageIcon[] sprites = new ImageIcon[3];
        for(int i=0; i<sprites.length;i++){
            sprites[i]=new ImageIcon("pac"+(i+1)+suffix+".png");
            System.out.println("pac"+(i+1)+".png");
        }
        return sprites;
    }

    public void advanceSprite(){
        if(currSpriteNum>=2)
            currSpriteNum=0;
        else
            currSpriteNum++;
        currSprite=currSpriteSheet[currSpriteNum];
    }
}
