
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
public class Game{

    int frameLength = 200; //miliseconds
    int points=0;
    int life=1;

    PowerType activePower;
    boolean isPowerActive;
    boolean checkPower;

    final JFrame frame;
    Map map;
    Player player;
    Enemy[] enemies;
    Thread logicThread;
    Thread enemyThread;
    Thread infoThread;
    Thread mapRefresherThread;
    Thread pointUpdater;
    Thread powerUpSpawnThread;
    Thread powerUpEventThread;
    moveDirection playermove;

    Thread endGameThread;
    int pelletCount = 0;

    public Game(int n) {
        checkPower = true;
        isPowerActive=false;
        //MAP READING
        enemies = new Enemy[4];
        playermove = moveDirection.NONE;
        switch (n) { //reading map based on choice in level select
            case 1:
                map = readMap("map1.txt");
                break;
            case 2:
                map = readMap("map2.txt");
                break;
            case 3:
                map = readMap("map3.txt");
                break;
            case 4:
                map = readMap("map4.txt");
                break;
            case 5:
                map = readMap("map5.txt");
                break;
        }

        int ghostCount = 0;
        for(int i=0;i<map.tiles.length;i++){
            if(map.tiles[i]=='P'){
                player = new Player(i % map.width, i / map.width);
            }
            if(map.tiles[i]=='R'){
                enemies[ghostCount] = new Enemy(i % map.width, i / map.width, 'R');
                ghostCount++;
            }
            if(map.tiles[i]=='G'){
                enemies[ghostCount] = new Enemy(i % map.width, i / map.width, 'G');
                ghostCount++;
            }
            if(map.tiles[i]=='B'){
                enemies[ghostCount] = new Enemy(i % map.width, i / map.width, 'B');
                ghostCount++;
            }
            if(map.tiles[i]=='Y'){
                enemies[ghostCount] = new Enemy(i % map.width, i / map.width, 'Y');
                ghostCount++;
            }
        }

        //COMPONENT CREATION
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Game");

        // POINT/TIME DISPLAY
        JPanel infoPanel = new JPanel();
        JLabel score = new JLabel("Score: "+points);
        JLabel time = new JLabel("Time: 0");
        JLabel lives = new JLabel("Lives: "+life);
        JPanel powerPanel = new JPanel();
        powerPanel.setLayout(new GridLayout(2,-1));
        JLabel powerLabel = new JLabel("Active power:");
        JLabel powerTypeLabel = new JLabel("None");
        infoPanel.setLayout(new GridLayout(4,-1));
        infoPanel.add(score);
        infoPanel.add(time);
        infoPanel.add(lives);
        powerPanel.add(powerLabel);
        powerPanel.add(powerTypeLabel);
        infoPanel.add(powerPanel);

        infoPanel.setPreferredSize(new Dimension(150,-1));

        //MAP DISPLAY
        char[] tiles = createTileMap(map.tiles);
        char[] objects = createObjectMap(map.tiles);
        char[] entities = createEntityMap(map.tiles);
        char[] powerUp = createPowerUpMap(map.tiles);
        JPanel mapPanel = new JPanel();
        createMapPanel(tiles,objects,entities,powerUp,map,player,mapPanel); //create GridLayout of the map in mapPanel

        //pellet count
        for(char e : objects){
            if(e == 'F') pelletCount++;
        }
        System.out.println("pellet count: "+pelletCount);

        //COMPONENT PLACEMENT
        frame.add(mapPanel,BorderLayout.CENTER);
        frame.add(infoPanel,BorderLayout.EAST);
        frame.setBackground(Color.BLACK);
        mapPanel.setBackground(Color.BLACK);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();

        //RESPONSIBLE FOR UPDATING INFO PANEL
        infoThread = new Thread(() -> {
            int i=0;
            for(;!infoThread.isInterrupted();i++){
                try{
                    Thread.sleep(1000);
                    synchronized(infoPanel){
                        time.setText("Time: "+i);
                        lives.setText("Lives: "+life);
                    }
                }catch (InterruptedException ex){
                    System.out.println("time stopped");
                    break;
                }finally{
                    time.setText("Time: "+i);
                }
                //System.out.println("time not paused");
            }
            System.out.println("closing time");

        });

        //RESPONSIBLE FOR MOVEMENT LOGIC
        logicThread = new Thread(() -> {
            while(!logicThread.isInterrupted()) {
                try {
                    Thread.sleep(frameLength);
                    synchronized(frame){
                        player.move(playermove,map,entities);
                        playermove = moveDirection.NONE;

                        if(objects[player.posX+player.posY* map.width]=='F'){ //is it a pellet
                            objects[player.posX+player.posY*map.width]='.';
                            points++;
                            pelletCount--;
                            if(pelletCount==0){
                                refillPellets(map.tiles,objects,pelletCount);
                            }
                        }

                        for(Enemy e : enemies){ //collision with enemies
                            if(e.posX == player.posX && e.posY == player.posY && player.invincible==false) {
                                life--;
                                activePower=PowerType.INVIS; //grace period on hit
                                isPowerActive=true; //grace period on hit
                            }
                        }

                        if(powerUp[player.posX+player.posY*map.width]!='.'){
                            if(isPowerActive) powerUp[player.posX+player.posY*map.width]='.'; //no double powerups at once
                            else{
                                activePower=determinePower(powerUp[player.posX+player.posY*map.width]);
                                isPowerActive=true;
                                powerUp[player.posX+player.posY*map.width]='.';
                            }
                        }

                        frame.notifyAll();
                    }
                    //System.out.println("x : "+player.posX+" y: "+player.posY);
                } catch (InterruptedException ex) {
                    System.out.println("logic stopped");
                    break;
                }
                //System.out.println("checking logic");
            }
            System.out.println("closing logic");

        });

        enemyThread = new Thread(() -> {
            while(!enemyThread.isInterrupted()){
                try{
                    Thread.sleep(frameLength);
                    synchronized(frame){
                        frame.wait();
                        for(Enemy e : enemies){
                            e.move(map,entities);
                        }
                        for(Enemy e : enemies){ //collision with enemies
                            if(e.posX == player.posX && e.posY == player.posY && player.invincible==false) {
                                life--;
                                activePower=PowerType.INVIS; //grace period on hit
                                isPowerActive=true; //grace period on hit
                            }
                        }
                        frame.notifyAll();
                    }
                }catch(InterruptedException ex){
                    System.out.println("Enemy interrupted");
                    break;
                }
            }
            System.out.println("enemy ending");
        });

        //RESPONSIBLE FOR MAP DISPLAY
        mapRefresherThread = new Thread(() -> { //this recreates the mapPanel every frameLength
            while(!mapRefresherThread.isInterrupted()){
                try{
                    Thread.sleep(frameLength);
                    synchronized(frame){
                        frame.remove(mapPanel);
                        createMapPanel(tiles,objects,entities,powerUp,map,player,mapPanel);
                        frame.add(mapPanel,BorderLayout.CENTER);
                        frame.invalidate();
                        frame.validate();
                        frame.setLocationRelativeTo(null);
                        frame.notifyAll();
                    }
                }catch(InterruptedException ex){
                    System.out.println("map stopped");
                    break;
                }
            }
            System.out.println("closing map");

        });

        //RESPONSIBLE FOR TIME TRACKING
        pointUpdater = new Thread(()->{
            while(!pointUpdater.isInterrupted()){
                try{
                    Thread.sleep(0);
                    synchronized(frame){
                        score.setText("Score: "+points);
                        frame.notify();
                    }
                }catch(InterruptedException ex){
                    System.out.println("point stopped");
                    break;
                } finally{
                    score.setText("Score: "+points);
                }
            }
            System.out.println("point stopped");
        });

        //RESPONSIBLE FOR SAVING SCORE
        endGameThread = new Thread(() -> {
             boolean keepAlive=true;

            while(keepAlive){ // this is just to guarantee termination on exception, reused for termination
                try{
                    Thread.sleep(frameLength);
                    if(life<=0){
                        logicThread.interrupt();
                        enemyThread.interrupt();
                        infoThread.interrupt();
                        pointUpdater.interrupt();
                        mapRefresherThread.interrupt();
                        powerUpSpawnThread.interrupt();
                        powerUpEventThread.interrupt();
                        String d = JOptionPane.showInputDialog("Game Over\n Score: "+points+"\nInput name:");
                        Score result = new Score(d,points);
                        saveScore(result,frame);
                        life=1;
                        keepAlive=false;
                        checkPower=false;
                        frame.dispose();
                    }
                }catch(InterruptedException ex){
                    keepAlive=false;
                    checkPower=false;
                    break;
                }
            }
            System.out.println("closing endGame");
        });

        //RESPONSIBLE FOR SPAWNING POWERUPS
        powerUpSpawnThread = new Thread(()-> {
            while(checkPower){
                try{
                    Thread.sleep(5000);
                    for(Enemy e : enemies){
                        int chanceToSpawn = (int)(Math.random()*4);
                        if(chanceToSpawn==0 && powerUp[e.posX+e.posY* map.width]=='.'){
                            int powerType = (int)(Math.random()*5);
                            switch(powerType){
                                //i - invis ; h - hp ; x - double ; w - walk through walls ; g - reset ghost
                                case 0:
                                    powerUp[e.posX+e.posY* map.width]='i';
                                    break;
                                case 1:
                                    powerUp[e.posX+e.posY* map.width]='h';
                                    break;
                                case 2:
                                    powerUp[e.posX+e.posY* map.width]='x';
                                    break;
                                case 3:
                                    powerUp[e.posX+e.posY* map.width]='w';
                                    break;
                                case 4:
                                    powerUp[e.posX+e.posY* map.width]='g';
                                    break;
                            }
                        }
                    }
                }catch(InterruptedException ex){
                    break;
                }
            }
            System.out.println("closing powerSpawn");

        });

        //RESPONSIBLE FOR TRIGGERING POWERUPS
        powerUpEventThread = new Thread(()->{
            while(checkPower){
                try{
                    Thread.sleep(frameLength);
                    if(isPowerActive){
                        switch(activePower){
                            case DOUBLE:
                                points*=2;
                                break;
                            case HP:
                                ++life;
                                break;
                            case NUKE:
                                synchronized(frame){
                                    for(Enemy e : enemies){
                                        entities[e.posX+e.posY*map.width]='.';
                                        e.posX = e.originX;
                                        e.posY = e.originY;
                                    }
                                    frame.notifyAll();
                                }
                                break;
                            case INVIS:
                                try{
                                    powerTypeLabel.setText("Invincible");
                                    player.invincible = true;
                                    Thread.sleep(10000);
                                    player.invincible = false;
                                    powerTypeLabel.setText("None");
                                }catch (Exception e){
                                    player.invincible = false;
                                    powerTypeLabel.setText("None");
                                    break;
                                }
                                break;
                            case GHOST:
                                try{
                                    powerTypeLabel.setText("Noclip");
                                    player.ghostmode = true;
                                    Thread.sleep(5000);
                                    player.ghostmode = false;
                                    powerTypeLabel.setText("None");
                                } catch (Exception e){
                                    player.ghostmode = false;
                                    powerTypeLabel.setText("None");
                                    break;
                                }
                                break;
                        }
                        isPowerActive=false;
                    }
                }catch(Exception ex){
                    break;
                }
            }
            System.out.println("closing powerActive");

        });

        powerUpEventThread.start();
        powerUpSpawnThread.start();
        endGameThread.start();
        logicThread.start();
        enemyThread.start();
        infoThread.start();
        mapRefresherThread.start();
        pointUpdater.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closed");
                logicThread.interrupt();
                enemyThread.interrupt();
                infoThread.interrupt();
                mapRefresherThread.interrupt();
                pointUpdater.interrupt();
                endGameThread.interrupt();
                powerUpSpawnThread.interrupt();
                powerUpEventThread.interrupt();
                frame.dispose();
            }
        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    playermove = moveDirection.LEFT;
                }
                else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    playermove = moveDirection.RIGHT;
                }
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    playermove = moveDirection.UP;
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    playermove = moveDirection.DOWN;
                }
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    logicThread.interrupt();
                    enemyThread.interrupt();
                    infoThread.interrupt();
                    pointUpdater.interrupt();
                    mapRefresherThread.interrupt();
                    endGameThread.interrupt();
                    powerUpSpawnThread.interrupt();
                    powerUpEventThread.interrupt();
                    frame.dispose();
                }
            }
        });

    }

    public Map readMap(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            s = br.readLine();
            String[] dims_s = s.split(" ");
            int[] dims_i = new int[dims_s.length];
            for (int i = 0; i < dims_s.length; i++) {
                dims_i[i] = Integer.parseInt(dims_s[i]);
            }
            char[] arr = new char[dims_i[0] * dims_i[1]];
            int i = 0;
            while ((s = br.readLine()) != null) {
                for (int j = 0; j < s.length(); j++) {
                    arr[i] = s.charAt(j);
                    i++;
                }
            }
            return new Map(dims_i[0], dims_i[1], arr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,"map file missing");
            return null;
        }
    }

    public char[] createTileMap(char[] map){
        char[] res = new char[map.length];
        for(int i=0;i<map.length;i++){
            switch(map[i]){
                case '2':
                    res[i]='2';
                case '1':
                    res[i]='1';
                    break;
                case '0':
                    res[i]='0';
                    break;
                default:
                    res[i]='0';
                    break;
            }
        }
        return res;
    }

    public char[] createObjectMap(char[] map){
        char[] res = new char[map.length];
        for(int i=0;i<map.length;i++){
            switch(map[i]){
                case '0':
                    res[i]='F';
                    break;
                default:
                    res[i]='.';
                    break;
            }
        }
        return res;
    }

    public char[] createEntityMap(char[] map){
        char[] res = new char[map.length];
        for(int i=0;i<map.length;i++){
            if(map[i]=='P' || map[i]=='R' || map[i]=='B' || map[i]=='G' || map[i]=='Y') res[i]=map[i];
            else res[i]='.';
        }
        return res;
    }

    public void refillPellets(char[] map, char[] obj, int pelletCount){
        for(int i=0;i<map.length;i++){
            if(map[i]=='0'){
                obj[i]='F';
                pelletCount++;
            }
        }
    }

    public char[] createPowerUpMap(char[] map){
        char[] res = new char[map.length];
        for(int i=0;i<map.length;i++){
            res[i]='.';
        }
        return res;
    }

    public void createMapPanel(char[] tiles, char[] objects, char[] entities,char[] powerup, Map map, Player player, JPanel panel){
        //int j =0;
        for(Component e : panel.getComponents()){
            panel.remove(e);
            //j++;
        }
        //System.out.println(j+" components deleted");
        //PRIORITY SYSTEM: ENTITY > OBJECT > TILE
        panel.setLayout(new GridLayout(map.height,map.width));
        for(int i=0;i<tiles.length;i++){
            if(entities[i]!='.'){
                switch(entities[i]){
                    case 'P':
                        panel.add(player);
                        break;
                    case 'R':
                        panel.add(new JLabel(new ImageIcon("ghostred.png")));
                        break;
                    case 'B':
                        panel.add(new JLabel(new ImageIcon("ghostblue.png")));
                        break;
                    case 'Y':
                        panel.add(new JLabel(new ImageIcon("ghostyellow.png")));
                        break;
                    case 'G':
                        panel.add(new JLabel(new ImageIcon("ghostgreen.png")));
                        break;
                }
            }
            else{
                if(powerup[i]!='.'){
                    switch(powerup[i]){
                        case 'i':
                            panel.add(new JLabel(new ImageIcon("invis.png")));
                            break;
                        case 'h':
                            panel.add(new JLabel(new ImageIcon("1up.png")));
                            break;
                        case 'x':
                            panel.add(new JLabel(new ImageIcon("double.png")));
                            break;
                        case 'w':
                            panel.add(new JLabel(new ImageIcon("wallwalk.png")));
                            break;
                        case 'g':
                            panel.add(new JLabel(new ImageIcon("ghostclear.png")));
                            break;
                        default:
                            break;
                    }
                }
                else {
                    if(objects[i]=='F'){
                        panel.add(new JLabel(new ImageIcon("pellet.png")));
                    }
                    else{
                        if(tiles[i]=='1' || tiles[i]=='2'){
                            panel.add(new JLabel(new ImageIcon("wall.png")));
                        }
                        else{
                            panel.add(new JLabel(new ImageIcon("path.png")));
                        }
                    }
                }
            }
        }
    }

    public void saveScore(Score score, JFrame frame){
        File file = new File("scores.txt");
        ArrayList<Score> scores = new ArrayList<>();
        if(!file.exists()){
            try{
                scores.add(score);
                FileOutputStream fileout = new FileOutputStream(file);
                ObjectOutputStream objectout = new ObjectOutputStream(fileout);
                objectout.writeObject(scores);
                objectout.close();
                fileout.close();
            }catch(IOException ex){
                JOptionPane.showMessageDialog(frame,"IO Exception");
            }
        }
        else{
            try{
                FileInputStream filein = new FileInputStream(file);
                ObjectInputStream objectin = new ObjectInputStream(filein);
                scores = (ArrayList<Score>)objectin.readObject();
                scores.add(score);
                objectin.close();
                filein.close();
                Collections.sort(scores);
                FileOutputStream fileout = new FileOutputStream(file);
                ObjectOutputStream objectout = new ObjectOutputStream(fileout);
                objectout.writeObject(scores);
                objectout.close();
                fileout.close();

            }catch(IOException ex){
                JOptionPane.showMessageDialog(frame,"IO Exception");
            }catch(ClassNotFoundException ex){
                JOptionPane.showMessageDialog(frame,"ClassNotFound Exception");
            }
        }
    }

    public PowerType determinePower(char tile){
        switch(tile){
            case 'i':
                return PowerType.INVIS;
            case 'w':
                return PowerType.GHOST;
            case 'g':
                return PowerType.NUKE;
            case 'h':
                return PowerType.HP;
            case 'x':
                return PowerType.DOUBLE;
            default:
                return null;
        }
    }
}
