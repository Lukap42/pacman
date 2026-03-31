import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Highscore extends JFrame{
    public Highscore(){
        Boolean show=true;

        DefaultListModel<String> scoreModel = new DefaultListModel<>();
        ArrayList<Score> aList = new ArrayList<>();
        try{
            FileInputStream filein = new FileInputStream("scores.txt");
            ObjectInputStream objectin = new ObjectInputStream(filein);
            aList=(ArrayList<Score>)objectin.readObject();
        }catch(IOException ex){
            JOptionPane.showMessageDialog(this,"The score file is missing, try playing the game first!");
            show=false;
        }catch(ClassNotFoundException ex){
            System.out.println("CLASS NOT FOUND EXCEPTION");
        }

        if(show){
            int i=0;
            for(Score s : aList){
                scoreModel.add(i,"NAME: "+s.name + "    SCORE: "+s.points);
                i++;
            }

            JList<String> list = new JList<>();
            list.setModel(scoreModel);
            list.setPreferredSize(new Dimension(800,800));
            JScrollPane scroll = new JScrollPane(list);
            add(scroll);

            setVisible(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
        }
    }
}
