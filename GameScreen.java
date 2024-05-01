package Java_Centipede;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen {
    private static final int PANEL_WIDTH = 1936;
    private static final int PANEL_HEIGHT = 1056;
    private static final int MOVE_AMOUNT = 5;

    private static int x = PANEL_WIDTH / 2; // Initial x position of the player
    private static int y = PANEL_HEIGHT / 2 + 300; // Initial y position of the player

    // List to store active bullets
    private static List<Point> bullets = new ArrayList<>();
    private static final int BULLET_SIZE = 5; // Adjust bullet size
    private static final int BULLET_SPEED = 10;
    public static Graphics g2d;

    private static ArrayList<Integer> mushroomX;
    private static ArrayList<Integer> mushroomY;
    private static ArrayList<String> mushroomStatus; // Store status of each mushroom
    public static String[][] centipedeMap;

    public static void main(String[] args) {
        mushroomX = new ArrayList<>();
        mushroomY = new ArrayList<>();
        mushroomStatus = new ArrayList<>();
        centipedeMap = new String[1936][1056];

        for (int i = 0; i < 50; i++) {
            mushroomX.add((int) (Math.random() * (PANEL_WIDTH - 6))); // Random x within the first half of the panel
            mushroomY.add((int) (Math.random() * (PANEL_HEIGHT - 700)) + (PANEL_HEIGHT / 11 + 5)); // Random y within the panel
            mushroomStatus.add("Full"); // Initially, all mushrooms are full
        }
        SwingUtilities.invokeLater(GameScreen::createAndShowGUI);
    }

    private static void createAndShowGUI() {

        JFrame outerFrame = new JFrame("Outer Frame");

        outerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        outerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        outerFrame.setVisible(true);
        outerFrame.setLayout(new BorderLayout());
        System.out.println(outerFrame.getSize());

        JPanel innerFrame = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g2d = (Graphics2D) g.create();

                int shadowHeight = 5; // Height of the shadow
                int marginSize = getHeight() / 11; // Size of the margin

                //getting the images
                ImageIcon playerIcon = new ImageIcon(getClass().getResource("blackCursor.png"));
                Image playerImage = playerIcon.getImage();

                // Draw top shadow
                g2d.setColor(Color.GREEN);
                g2d.fillRect(0, marginSize - shadowHeight, getWidth(), shadowHeight);

                // Draw bottom shadow
                g2d.fillRect(0, getHeight() - marginSize, getWidth(), shadowHeight);

                // Draw player
                g2d.drawImage(playerImage, x, y, this);

                // Draw mushrooms
                Iterator<Integer> iteratorX = mushroomX.iterator();
                Iterator<Integer> iteratorY = mushroomY.iterator();
                Iterator<String> iteratorStatus = mushroomStatus.iterator();
                while (iteratorX.hasNext() && iteratorY.hasNext() && iteratorStatus.hasNext()) {
                    int mx = iteratorX.next();
                    int my = iteratorY.next();
                    String status = iteratorStatus.next();
                    if (status.equals("Full")) {
                        loadMushroom(mx, my, "Full", g2d);
                    }
                }

                // Draw bullets
                g2d.setColor(Color.BLUE);
                for (Point bullet : bullets) {
                    g2d.fillRect(bullet.x, bullet.y, BULLET_SIZE, BULLET_SIZE);
                }

                g2d.dispose();
            }

            private void loadMushroom(int x, int y, String status, Graphics g) {
                //mushrooms will take 4 hits to destroy. Will only have cases for 3 of them and for the one which is destroyed will have different process.
                Graphics2D g2d = (Graphics2D) g;
                switch (status) {
                    case "Full":
                        ImageIcon stage0Mushroom = new ImageIcon(getClass().getResource("Stage0Mushroom.png"));
                        Image stage0MushroomImage = stage0Mushroom.getImage();
                        g2d.drawImage(stage0MushroomImage, x, y, this);
                        break;
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
                    	if(x-5*MOVE_AMOUNT>=0)
                    		x -= 5*MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_RIGHT:
                    	if(x+5*MOVE_AMOUNT<=1936)
                    		x += 5*MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_UP:
                    	if(y-5*MOVE_AMOUNT>=(PANEL_HEIGHT / 11 + 5) )
                    		y -= 5*MOVE_AMOUNT;
                        break;
                    case KeyEvent.VK_DOWN:
                        if(y+5*MOVE_AMOUNT<= (1056-(PANEL_HEIGHT/11+5)))
                        	y+=5*MOVE_AMOUNT;
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
                Iterator<Point> bulletIterator = bullets.iterator();
                while (bulletIterator.hasNext()) {
                    Point bullet = bulletIterator.next();
                    bullet.y -= BULLET_SPEED; // Move bullets upwards

                    // Check for collision with mushrooms
                    Iterator<Integer> iteratorX = mushroomX.iterator();
                    Iterator<Integer> iteratorY = mushroomY.iterator();
                    Iterator<String> iteratorStatus = mushroomStatus.iterator();
                    while (iteratorX.hasNext() && iteratorY.hasNext() && iteratorStatus.hasNext()) {
                        int mx = iteratorX.next();
                        int my = iteratorY.next();
                        String status = iteratorStatus.next();
                        if (status.equals("Full") && bullet.x >= mx && bullet.x <= mx + 32 && bullet.y >= my && bullet.y <= my + 32) {
                            // Bullet hit a mushroom
                            iteratorX.remove(); // Remove the mushroom's x coordinate
                            iteratorY.remove(); // Remove the mushroom's y coordinate
                            iteratorStatus.remove(); // Remove the mushroom's status
                            
                            bulletIterator.remove(); // Remove the bullet
                            break; // Exit the loop after hitting one mushroom
                        }
                    }
                }
                innerFrame.repaint(); // Repaint panel after moving bullets
                try {
                    Thread.sleep(10); // Adjust speed of bullets
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        outerFrame.add(innerFrame, BorderLayout.CENTER);
        outerFrame.setVisible(true);
    }
}
