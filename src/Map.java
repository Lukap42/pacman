public class Map {
    int width;
    int height;
    char[] tiles;

    public Map(int w, int h, char[] arr){
        width=w;
        height=h;
        tiles=arr;
    }

    public char getTile(int x, int y){
        return tiles[x+y*width];
    }
}
