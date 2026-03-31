import java.io.Serializable;
import java.util.Comparator;

public class Score implements Serializable, Comparable<Score> {
    String name;
    int points;

    public Score(String name, int points){
        this.name = name;
        this.points = points;
    }

    public int compareTo(Score second){
        if(this.points == second.points) return 0;
        else if(this.points > second.points) return -1;
        else return 1;
    }
}
