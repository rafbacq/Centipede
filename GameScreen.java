package Java_Centipede;

import javax.swing.*;

import Java_Centipede.LilyGameScreen.Spider;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameScreen {
    private static final int PANEL_WIDTH = 1936;
    private static ArrayList<String> mushroomStatus; // Store status of each mushroom
    private static final int PANEL_HEIGHT = 1056;
    private static Timer smoothMovementTimer;
    private static int moveDirectionX = 0; // -1 for left, 1 for right, 0 for no movement
    private static int moveDirectionY = 0; // -1 for up, 1 for down, 0 for no movement
    private static final int MOVE_AMOUNT = 5; // Adjust the speed of movement

    private static int x = PANEL_WIDTH / 2; // Initial x position of the player
    private static int y = PANEL_HEIGHT / 2 + 300; // Initial y position of the player

    private static int mouseXOffset; // Offset between mouse click position and player position
    private static int mouseYOffset;
    private static boolean isDragging = false; // Flag to indicate if the player is being dragged

    // List to store active bullets
    private static List<Point> bullets = new ArrayList<>();
    private static final int BULLET_SIZE = 5; // Adjust bullet size
    private static final int BULLET_SPEED = 10;

    private static ArrayList<Integer> mushroomX;
    private static ArrayList<Integer> mushroomY;
    private static ArrayList<Integer> mushroomHits; // Store number of hits for each mushroom

    private static ArrayList<Rectangle> mushroomBorders;
    private static Spider spider;

    public static int gameScore;

    private static int playerLives = 3;
    private static boolean isPlayerDead = false;
    private static Timer deathAnimationTimer;

    private static int scoreFontSize = 24;
    private static final int MAX_FONT_SIZE = 32;
    private static final int MIN_FONT_SIZE = 24;
    private static final int FONT_ANIMATION_STEP = 2;
    private static JPanel innerFrame;
    private static List<LilyCentipedeBody> centipedes = new ArrayList<>();

    public static void main(String[] args) {
        mushroomX = new ArrayList<>();
        mushroomY = new ArrayList<>();
        mushroomHits = new ArrayList<>();
        mushroomStatus = new ArrayList<>();
        mushroomBorders = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            mushroomX.add((int) (Math.random() * (PANEL_WIDTH - 6))); // Random x within the first half of the panel
            mushroomY.add((int) (Math.random() * (PANEL_HEIGHT - 700)) + (PANEL_HEIGHT / 11 + 5)); // Random y within the panel
            mushroomHits.add(0); // Initially, all mushrooms have 0 hits
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

        innerFrame = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Draw top and bottom borders
                g2d.setColor(Color.GREEN);
                int borderMarginBottom = (int) (PANEL_HEIGHT * 0.135); // 20% of panel height
                int borderMarginTop = (int)(PANEL_HEIGHT * 0.09);

                // Top border
                g2d.fillRect(0, borderMarginTop, PANEL_WIDTH, 5);

                // Bottom border
                g2d.fillRect(0, PANEL_HEIGHT - borderMarginBottom - 5, PANEL_WIDTH, 5);

                // Draw player with death animation effect
                if (isPlayerDead) {
                    float alpha = 1.0f - (deathAnimationTimer.getDelay() / 100.0f); // Calculate alpha based on time
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                }
                ImageIcon playerIcon = new ImageIcon(getClass().getResource("blackCursor.png"));
                Image playerImage = playerIcon.getImage();
                g2d.drawImage(playerImage, x, y, this);

                // Reset alpha composite to default
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));


                // Clear and update the mushroomBorders list
                mushroomBorders.clear();

                // Draw mushrooms and update mushroomBorders
                for (int i = 0; i < mushroomX.size(); i++) {
                    int mx = mushroomX.get(i);
                    int my = mushroomY.get(i);
                    int hits = mushroomHits.get(i);
                    

                    Rectangle mushroomRect = new Rectangle(mx, my, 20, 20);
                    mushroomBorders.add(mushroomRect);

                    // Draw mushrooms based on hit stage
                    switch (hits) {
                        case 0:
                            ImageIcon stage0Mushroom = new ImageIcon(getClass().getResource("Stage0Mushroom.png"));
                            Image stage0MushroomImage = stage0Mushroom.getImage();
                            g2d.drawImage(stage0MushroomImage, mx, my, this);
                            break;
                        case 1:
                            ImageIcon stage1Mushroom = new ImageIcon(getClass().getResource("Stage1Mushroom.png"));
                            Image stage1MushroomImage = stage1Mushroom.getImage();
                            g2d.drawImage(stage1MushroomImage, mx, my, this);
                            break;
                        case 2:
                            ImageIcon stage2Mushroom = new ImageIcon(getClass().getResource("Stage2Mushroom.png"));
                            Image stage2MushroomImage = stage2Mushroom.getImage();
                            g2d.drawImage(stage2MushroomImage, mx, my, this);
                            break;
                        case 3:
                            ImageIcon stage3Mushroom = new ImageIcon(getClass().getResource("Stage3Mushroom.png"));
                            Image stage3MushroomImage = stage3Mushroom.getImage();
                            g2d.drawImage(stage3MushroomImage, mx, my, this);
                            break;
                        default:
                            // If mushroom is destroyed (hits > 3), don't draw it
                            break;
                    }
                }

                // Draw bullets
                g2d.setColor(Color.BLUE);
                for (Point bullet : bullets) {
                    g2d.fillRect(bullet.x, bullet.y, BULLET_SIZE, BULLET_SIZE);
                }

                // Draw game score
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, scoreFontSize));
                String scoreText = "Score: " + gameScore;
                FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
                int scoreX = (PANEL_WIDTH - metrics.stringWidth(scoreText)) / 2;
                int scoreY = metrics.getHeight();
                g2d.drawString(scoreText, scoreX, scoreY);

                // Draw player lives
                String livesText = "Lives: " + playerLives;
                int livesX = PANEL_WIDTH - metrics.stringWidth(livesText) - 10;
                int livesY = metrics.getHeight();
                g2d.drawString(livesText, livesX, livesY);
             // Draw centipedes
                for (LilyCentipedeBody centipede : centipedes) {
                    centipede.drawCentipede(g2d);
                }
                if (spider != null) {
                    spider.draw(g2d);
                }
                g2d.dispose();


            }
        };

        smoothMovementTimer = new Timer(10, new ActionListener() { // Adjusted to 10 ms for smoother movement
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check for collision before updating position
                if (checkNonCollision(x + moveDirectionX * MOVE_AMOUNT, y + moveDirectionY * MOVE_AMOUNT)) {
                    x += moveDirectionX * MOVE_AMOUNT;
                    y += moveDirectionY * MOVE_AMOUNT;
                }
                innerFrame.repaint();
            }
        });
        smoothMovementTimer.setRepeats(true);
        innerFrame.setBackground(Color.BLACK);

        innerFrame.setFocusable(true);
        innerFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        moveDirectionX = -1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveDirectionX = 1;
                        break;
                    case KeyEvent.VK_UP:
                        moveDirectionY = -1;
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDirectionY = 1;
                        break;
                    case KeyEvent.VK_SPACE: // Space bar pressed
                        // Add a new bullet position to the list
                        bullets.add(new Point(x + 10, y - 5)); // Adjust initial position of the bullet
                        break;
                    case KeyEvent.VK_P: // "p" key pressed
                        if (!isPlayerDead) {
                            playerLives--;
                            isPlayerDead = true;
                            startDeathAnimation();
                        }
                        break;
                }
                // Start smooth movement timer when a movement key is pressed
                smoothMovementTimer.start();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        moveDirectionX = 0;
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        moveDirectionY = 0;
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Not used
            }
        });

        // Mouse press listener
        innerFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if the mouse click is on the player image
                if (e.getX() >= x && e.getX() <= x + 100 && e.getY() >= y && e.getY() <= y + 100) {
                    if (!isDragging) {
                        mouseXOffset = e.getX() - x;
                        mouseYOffset = e.getY() - y;
                        isDragging = true;
                    } else {
                        isDragging = false;
                    }
                }
            }
        });
     // Initialize centipedes
        centipedes.add(new LilyCentipedeBody(0, 87, 20, true));
        spider = new Spider((int) (Math.random() * PANEL_WIDTH), PANEL_HEIGHT / 2 + 300, 15, Color.GREEN, 6, 6); 

        // Mouse motion listener for dragging
        innerFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int newX = e.getX() - mouseXOffset;
                    int newY = e.getY() - mouseYOffset;
                    if (checkNonCollision(newX, newY)) {
                        x = newX;
                        y = newY;
                        innerFrame.repaint();
                    }
                }
            }
        });

        // Bullet animation thread
        new Thread(() -> {
            while (true) {
                int bulletIndex = 0;
                List<Point> bulletsToRemove = new ArrayList<>();
               

                while (bulletIndex < bullets.size()) {
                    Point bullet = bullets.get(bulletIndex);
                    bullet.y -= BULLET_SPEED;

                    boolean bulletRemoved = false;

                    int mushroomIndex = 0;
                    while (mushroomIndex < mushroomX.size()) {
                        int mx = mushroomX.get(mushroomIndex);
                        int my = mushroomY.get(mushroomIndex);

                        if (bullet.x >= mx && bullet.x <= mx + 20 && bullet.y >= my && bullet.y <= my + 20) {
                            // Bullet hit a mushroom
                            int hits = mushroomHits.get(mushroomIndex);
                            if (hits < 3) {
                                mushroomHits.set(mushroomIndex, hits + 1);
                            } else {
                                mushroomX.remove(mushroomIndex);
                                mushroomY.remove(mushroomIndex);
                                mushroomHits.remove(mushroomIndex);
                                gameScore += 200;

                                
                            }

                            bullets.remove(bulletIndex);
                            bulletRemoved = true;
                            break;
                        }

                        mushroomIndex++;
                    }

                    if (!bulletRemoved) {
                        bulletIndex++;
                   }
                 // Inside the bullet animation thread where collision is detected
                    for (LilyCentipedeBody centipede : centipedes) {
                        boolean hit = false;
                        for (int i = 0; i < centipede.getLength(); i++) {
                            if (bullet.x >= centipede.x[i] && bullet.x <= centipede.x[i] + LilyCentipedeBody.UNIT_SIZE &&
                                    bullet.y >= centipede.y[i] && bullet.y <= centipede.y[i] + LilyCentipedeBody.UNIT_SIZE) {
                                // Bullet hit a centipede segment
                                bulletsToRemove.add(bullet); // Mark the bullet for removal

                                // Place a mushroom at the hit location
                                mushroomX.add(centipede.x[i]);
                                mushroomY.add(centipede.y[i]);
                                mushroomStatus.add("Full");

                                // Split the centipede
                                if (i < centipede.getLength() - 1) {
                                    LilyCentipedeBody newCentipede = new LilyCentipedeBody(centipede.x[i + 1], centipede.y[i + 1], centipede.getLength() - i - 1, centipede.velX[0] < 0);
                                    newCentipede.moveDownAndSwitchDirection();
                                    centipedes.add(newCentipede);
                                }
                                centipede.shorten(i);
                                hit = true;
                                break; // Exit the loop after hitting one segment
                            }
                        }
                        if (hit) {
                            break; // Stop checking further if a hit was registered
                        }
                    }

                }

                // Remove bullets that have hit something
                bullets.removeAll(bulletsToRemove);

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
 // Spider movement thread
    new Thread(() -> {
        while (true) {
            spider.update();
            innerFrame.repaint(); // Repaint panel after moving the spider
            // Check for collision between spider and player
            if (spider.posX < x + 32 && spider.posX + spider.size > x && spider.posY < y + 32 && spider.posY + spider.size > y) {
                JOptionPane.showMessageDialog(innerFrame, "You lose");
                System.exit(0); // Exit the game
            }
            try {
                Thread.sleep(15); // Adjust speed of the spider
            } catch (InterruptedException ex) { 
                ex.printStackTrace();
            }
        }
    }).start();
    /*for(LilyCentipedeBody b: centipedes) {
    	new Thread(() -> {
    		b.move();
    		innerFrame.repaint();
    		
    }).start();*/
    
    outerFrame.add(innerFrame, BorderLayout.CENTER);
    outerFrame.setVisible(true);
    innerFrame.requestFocusInWindow();
}

    public static class Spider {
        private int posX, posY, size;
        private Color color;
        private int vx, vy;
        private int stepsRemaining;
        private int stage; // 0 for "M" shape, 1 for vertical movement

        public Spider(int posX, int posY, int size, Color color, int vx, int vy) {
            this.posX = posX;
            this.posY = posY;
            this.size = size;
            this.color = color;
            this.vx = vx;
            this.vy = vy;
            this.stepsRemaining = (int) (Math.random() * 50) + 5;
            this.stage = 0;
        }

        public void update() {
            if (stage == 0) {
                if (stepsRemaining > 0) {
                    posX += vx;
                    posY -= vy;
                    stepsRemaining--;
                } else {
                    stage = 1;
                    stepsRemaining = (int) (Math.random() * 50) + 5;
                }
            } else if (stage == 1) {
                if (stepsRemaining > 0) {
                    // Move vertically up
                    posY -= vy;
                    stepsRemaining--;
                } else {
                    // If in the top half of the frame, bounce back towards the bottom
                    if (posY < PANEL_HEIGHT / 2) {
                        vy = -vy;
                    }
                    stage = 2; // Move to diagonally down stage
                    stepsRemaining = (int) (Math.random() * 50) + 5;
                }
            } else {
                if (stepsRemaining > 0) {
                    // Move diagonally down
                    posX += vx;
                    posY += vy;
                    stepsRemaining--;
                } else {
                    // Change stage and reset steps
                    stage = 0; // Move back to "M" shape stage
                    stepsRemaining = (int) (Math.random() * 50) + 5; // Random number of steps for next movement
                }
            }

            // Bounce off walls
            if (posX <= 0 || posX + size >= PANEL_WIDTH) {
                vx = -vx;
            }
            if (posY <= PANEL_HEIGHT / 2 || posY + size >= 922) {
                vy = -vy;
            }
        }

        public void draw(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillOval(posX - 5, posY - 5, size + 10, size + 10);

            g.setColor(color);
            g.fillOval(posX, posY, size, size);
            //left side
            g.drawLine(posX + 2, posY, posX - 3, posY - 3);
            g.drawLine(posX, posY + 5, posX - 5, posY + 5);
            g.drawLine(posX, posY + 9, posX - 5, posY + 9);
            g.drawLine(posX + 2, posY + 12, posX - 3, posY + 15);

            //right side
            g.drawLine(posX + 12, posY, posX + 17, posY - 3);
            g.drawLine(posX + 15, posY + 5, posX + 19, posY + 5);
            g.drawLine(posX + 15, posY + 9, posX + 19, posY + 9);
            g.drawLine(posX + 12, posY + 12, posX + 17, posY + 15);
        }
    }



    private static void startDeathAnimation() {
        deathAnimationTimer = new Timer(80, new ActionListener() {
            float alpha = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f; // Adjust the decrement rate for desired speed
                if (alpha <= 0) {
                    alpha = 0;
                    isPlayerDead = false;
                    x = PANEL_WIDTH / 2;
                    y = PANEL_HEIGHT / 2 + 300;
                    ((Timer) e.getSource()).stop();
                }
                innerFrame.repaint();
            }
        });
        deathAnimationTimer.start();
    }

    private static boolean checkNonCollision(int newX, int newY) {
        Rectangle playerRect = new Rectangle(newX, newY, 40, 45);
        for (Rectangle mushroomRect : mushroomBorders) {
            if (playerRect.intersects(mushroomRect)) {
                return false;
            }
        }
        return true;
    }
}
