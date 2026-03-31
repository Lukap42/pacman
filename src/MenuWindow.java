import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuWindow extends JFrame{
    public MenuWindow(){
        JPanel logoPanel = new JPanel();

        ImageIcon logoIcon = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));

        JButton game = new JButton("New Game");
        JButton score = new JButton("Highscore");
        JButton exit = new JButton("Exit");

        game.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new LevelSelect());
            }
        });

        score.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Highscore());
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                int d = JOptionPane.showConfirmDialog(
                        getRootPane(), "Are you sure"
                );
                if (d == JOptionPane.YES_OPTION){
                    dispose();
                }
                else{

                }
            }
        });


        buttonPanel.add(game);
        buttonPanel.add(score);
        buttonPanel.add(exit);
        buttonPanel.setPreferredSize(new Dimension(-1,100));

        setLayout(new BorderLayout());

        add(buttonPanel,BorderLayout.CENTER);
        add(logoPanel,BorderLayout.NORTH);

        setBackground(Color.BLACK);
        setTitle("Pac-Man");

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
