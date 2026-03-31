import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class LevelSelect extends JFrame {
    public static int level=0;

    public LevelSelect(){
        JPanel panel = new JPanel();

        JRadioButton size1 = new JRadioButton("size1");
        size1.setActionCommand("1");
        JRadioButton size2 = new JRadioButton("size2");
        size2.setActionCommand("2");
        JRadioButton size3 = new JRadioButton("size3");
        size3.setActionCommand("3");
        JRadioButton size4 = new JRadioButton("size4");
        size4.setActionCommand("4");
        JRadioButton size5 = new JRadioButton("size5");
        size5.setActionCommand("5");

        ButtonGroup levels = new ButtonGroup();
        levels.add(size1);
        levels.add(size2);
        levels.add(size3);
        levels.add(size4);
        levels.add(size5);

        panel.setLayout(new GridLayout(-1,5));

        panel.add(size1);
        panel.add(size2);
        panel.add(size3);
        panel.add(size4);
        panel.add(size5);

        JButton confirm = new JButton("Start Game");
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(levels.getSelection()!=null){
                    level = Integer.parseInt(levels.getSelection().getActionCommand());
                    SwingUtilities.invokeLater(() -> new Game(level));
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null,"No option selected","No option selected",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        setLayout(new GridLayout(2,1));
        add(panel);
        add(confirm);
        setTitle("Level size select");
        setVisible(true);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }


}
