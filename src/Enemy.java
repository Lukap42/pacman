public class Enemy extends Entity {

    char color;
    public Enemy(int x, int y, char type){
        super(x,y);
        color = type;
    }

    public void move(Map map, char[] entitymap){
        int willMove = (int)(Math.random()*2);
        if(willMove==0){
            int movement = (int)(Math.random()*4);
            switch(movement){
                case 0: //up
                    if(map.getTile(posX,posY-1)!='1' && map.getTile(posX,posY-1)!='2'){
                        if(entitymap[posX+posY*map.width]==color)
                            entitymap[posX+posY*map.width]='.';
                        posY--;
                        entitymap[posX+posY*map.width]=color;
                    }
                    break;
                case 1:
                    if(map.getTile(posX,posY+1)!='1' && map.getTile(posX,posY+1)!='2'){
                        if(entitymap[posX+posY*map.width]==color)
                            entitymap[posX+posY*map.width]='.';
                        posY++;
                        entitymap[posX+posY*map.width]=color;
                    }
                    break;
                case 2:
                    if(posX!=0 && map.getTile(posX-1,posY)!='1' && posX!=0 && map.getTile(posX-1,posY)!='2') {
                        if(entitymap[posX+posY*map.width]==color)
                            entitymap[posX+posY*map.width]='.';
                        posX--;
                        entitymap[posX+posY*map.width]=color;
                    }
                    break;
                case 3:
                    if(posX!=map.width-1 && map.getTile(posX+1,posY)!='1' && posX!=map.width-1 && map.getTile(posX+1,posY)!='2') {
                        if(entitymap[posX+posY*map.width]==color)
                            entitymap[posX+posY*map.width]='.';
                        posX++;
                        entitymap[posX+posY*map.width]=color;
                    }
                    break;
            }
        }
    }
}
