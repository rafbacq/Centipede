package Java_Centipede;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {
    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 800;
    private static final int MOVE_AMOUNT = 5;

    private static int x = PANEL_WIDTH / 2; // Initial x position of the player
    private static int y = PANEL_HEIGHT / 2; // Initial y position of the player

    // List to store active bullets
    private static List<Point> bullets = new ArrayList<>();
    private static final int BULLET_SIZE = 5; // Adjust bullet size
    private static final int BULLET_SPEED = 10;
    public static Graphics g2d;
   
    private static ArrayList<Integer> mushroomX;
    private static ArrayList<Integer> mushroomY;
    public static String [][] centipedeMap;
 

    public static void main(String[] args) {
    	mushroomX = new ArrayList<>();
    	mushroomY = new ArrayList<>();
    	//centipedeMap = new String[]
    	
    	/*for (int i = 0; i < 100; i++) {
            mushroomX.add((int) (Math.random() * (PANEL_WIDTH))); // Random x within the first half of the panel
            mushroomY.add((int) (Math.random() * PANEL_HEIGHT +(PANEL_HEIGHT/11 +5))); // Random y within the panel
            
            
        }*/
        SwingUtilities.invokeLater(GameScreen::createAndShowGUI);
    }

    private static void createAndShowGUI() {
    	
        JFrame outerFrame = new JFrame("Outer Frame");
        
        
        
        outerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        outerFrame.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        outerFrame.setLayout(new BorderLayout());
        
        
        JPanel innerFrame = new JPanel() {
            @Override
            
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g2d = (Graphics2D) g.create();


                int shadowHeight = 5; // Height of the shadow
                int marginSize = getHeight() / 11; // Size of the margin
                
                
                
                int playerWidth = getWidth() / 10; // Adjust player width percentage
                int playerHeight = getHeight() / 10; // Adjust player height percentage
                
                
                //getting the images
                ImageIcon playerIcon = new ImageIcon(getClass().getResource("blackCursor.png"));
                Image playerImage = playerIcon.getImage();
                
                //Image fullMushroom = (new ImageIcon(getClass().getResource("fullMushroom.png")).getImage());
                //Image stage1Mushroom = (new ImageIcon(getClass().getResource("stage1Mushroom.png")).getImage());
                //Image stage2Mushroom = (new ImageIcon(getClass().getResource("stage2Mushroom.png")).getImage());
                //Image stage3Mushroom = (new ImageIcon(getClass().getResource("stage3Mushroom.png")).getImage());
                
                
                
                
                // Draw top shadow
                g2d.setColor(Color.GREEN);
                g2d.fillRect(0, marginSize - shadowHeight, getWidth(), shadowHeight);

                // Draw bottom shadow
                g2d.fillRect(0, getHeight() - marginSize, getWidth(), shadowHeight);

                // Draw player
                g2d.drawImage(playerImage, x, y, this);
                
                // Draw mushrooms
              
                /*for (int i = 0; i < 100; i++) {
                        loadMushroom(mushroomX.get(i), mushroomY.get(i), "Full", g2d);
                       
                }*/

                // Draw bullets
                g2d.setColor(Color.BLUE);
                for (Point bullet : bullets) {
                    g2d.fillRect(bullet.x, bullet.y, BULLET_SIZE, BULLET_SIZE);
                }

                g2d.dispose();
            }
            private void loadMushroom(int x, int y, String status, Graphics g)
            {
            	//mushrooms will take 4 hits to destroy. Will only have cases for 3 of them and for the one which is destroyed will have different process.
            	
                Graphics2D g2d = (Graphics2D) g;
            	switch(status)
            	{
            		case "Full": 
            			ImageIcon stage0Mushroom = new ImageIcon(getClass().getResource("Stage0Mushroom.png"));
                        Image stage0MushroomImage = stage0Mushroom.getImage();
            			g2d.drawImage(stage0MushroomImage, x, y, this);
//            			// Draw mushroom stem
//            			g2d.setColor(new Color(128, 64, 0)); // Brown color
//            			g2d.fillRect(100, 200, 40, 100);
//            			// Draw mushroom cap
//            			g2d.setColor(new Color(255, 0, 0)); // Red color
//            			g2d.fillArc(60, 100, 120, 120, 0, 180);
//            			// Draw white spots on the cap
//            			g2d.setColor(Color.WHITE);
//            			g2d.fillOval(90, 80, 20, 20);
//            			g2d.fillOval(110, 70, 30, 30);
//            			g2d.fillOval(140, 90, 25, 25);
//            			g2d.fillOval(100, 140, 20, 20);
//            			g2d.fillOval(130, 130, 15, 15);
            		case "3/4":
            			ImageIcon stage1Mushroom = new ImageIcon(getClass().getResource("Stage1Mushroom.png"));
                        Image stage1MushroomImage = stage1Mushroom.getImage();
            			g2d.drawImage(stage1MushroomImage, x, y, this);
//            			 // Draw mushroom stem
//            	        g2d.setColor(new Color(128, 64, 0)); // Brown color
//            	        g2d.fillRect(100, 200, 40, 100);
        //
//            	        // Draw mushroom cap
//            	        g2d.setColor(new Color(255, 0, 0)); // Red color
//            	        g2d.fillArc(60, 100, 120, 120, 0, 270); // Three-quarters of the cap
        //
//            	        // Draw white spots on the cap
//            	        g2d.setColor(Color.WHITE);
//            	        g2d.fillOval(90, 80, 20, 20);
//            	        g2d.fillOval(110, 70, 30, 30);
//            	        g2d.fillOval(140, 90, 25, 25);
//            	        g2d.fillOval(100, 140, 20, 20);
//            	        g2d.fillOval(130, 130, 15, 15);
            		case "1/2":
            			ImageIcon stage2Mushroom = new ImageIcon(getClass().getResource("Stage2Mushroom.png"));
                        Image stage2MushroomImage = stage2Mushroom.getImage();
            			g2d.drawImage(stage2MushroomImage, x, y, this);
//            			// Draw mushroom cap
//            	        g2d.setColor(new Color(255, 0, 0)); // Red color
//            	        g2d.fillArc(60, 100, 120, 120, 0, 270); // Three-quarters of the cap
        //
//            	        // Draw white spots on the cap
//            	        g2d.setColor(Color.WHITE);
//            	        g2d.fillOval(90, 80, 20, 20);
//            	        g2d.fillOval(110, 70, 30, 30);
//            	        g2d.fillOval(140, 90, 25, 25);
//            	        g2d.fillOval(100, 140, 20, 20);
//            	        g2d.fillOval(130, 130, 15, 15);
            		case "1/4": 
            			ImageIcon stage3Mushroom = new ImageIcon(getClass().getResource("Stage3Mushroom.png"));
                        Image stage3MushroomImage = stage3Mushroom.getImage();
            			g2d.drawImage(stage3MushroomImage, x, y, this);
//            			// Draw mushroom cap
//            	        g2d.setColor(new Color(255, 0, 0)); // Red color
//            	        int startAngle = 45; // Start angle for the arc
//            	        int arcAngle = 180; // Sweep angle for the arc
//            	        g2d.fillArc(60, 100, 120, 120, startAngle, arcAngle); // Half of the cap
        //
//            	        // Draw white spots on the cap
//            	        g2d.setColor(Color.WHITE);
//            	        g2d.fillOval(90, 80, 20, 20);
//            	        g2d.fillOval(110, 70, 30, 30);
//            	        g2d.fillOval(140, 90, 25, 25);
            	   
            	   
            	}
            }
        };
        innerFrame.setBackground(Color.BLACK);

        innerFrame.setFocusable(true); // Allow panel to receive focus
        innerFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        x -= MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        x += MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_UP:
                        y -= MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_DOWN:
                        y += MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_SPACE: // Space bar pressed
                        // Add a new bullet position to the list
                        bullets.add(new Point(x + 22, y - 5)); // Adjust initial position of the bullet
                        break;
                }
                innerFrame.repaint(); // Repaint panel after moving or shooting
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Bullet animation thread
        new Thread(() -> {
            while (true) {
                for (Point bullet : bullets) {
                    bullet.y -= BULLET_SPEED; // Move bullets upwards
                }
                innerFrame.repaint(); // Repaint panel after moving bullets
                try {
                    Thread.sleep(20); // Adjust speed of bullets
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        outerFrame.add(innerFrame, BorderLayout.CENTER);
        outerFrame.setVisible(true);
    }
    
    	
    }

