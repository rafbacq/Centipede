package Java_Centipede;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CentipedeBody extends JPanel implements ActionListener {
    public static final int UNIT_SIZE = 20;
    private static final int WIDTH = 1936;
    private static final int HEIGHT = 1056;
    private static final int DELAY = 100;
    private Timer timer;
    public int[] x;
    public int[] y;
    public int[] velX;
    public int[] velY;
    public int length;

    public CentipedeBody(int startX, int startY, int length, boolean movingRight) {
        this.length = length;
        this.x = new int[length];
        this.y = new int[length];
        this.velX = new int[length];
        this.velY = new int[length];

        for (int i = 0; i < length; i++) {
            x[i] = startX + i * UNIT_SIZE;
            y[i] = startY;
            velX[i] = movingRight ? UNIT_SIZE : -UNIT_SIZE;
            velY[i] = 0;
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public int getLength() {
        return length;
    }

    public void drawCentipede(Graphics g) {
        if (length <= 0) {
            return; // Avoid drawing if the length is zero or negative
        }
        g.setColor(Color.GREEN);
        for (int i = 0; i < length; i++) {
            g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
        g.setColor(Color.RED);
        g.fillOval(x[length - 1] + UNIT_SIZE / 4, y[length - 1] + UNIT_SIZE / 4 - 2, UNIT_SIZE / 4, UNIT_SIZE / 4); // Left eye
        g.fillOval(x[length - 1] + UNIT_SIZE / 4, 8 + y[length - 1] + UNIT_SIZE / 4, UNIT_SIZE / 4, UNIT_SIZE / 4); // Right eye
    }

    public boolean isCollisionWithPlayer(int playerX, int playerY) {
        for (int i = 0; i < getLength(); i++) {
            if (playerX >= x[i] && playerX <= x[i] + UNIT_SIZE &&
                playerY >= y[i] && playerY <= y[i] + UNIT_SIZE) {
                return true;
            }
        }
        return false;
    }

    public void move() {
        for (int i = 0; i < length; i++) {
            x[i] += velX[i];
            y[i] += velY[i];

            // Check for collision with mushrooms
            for (int j = 0; j < LilyGameScreen.mushroomX.size(); j++) {
                int mx = LilyGameScreen.mushroomX.get(j);
                int my = LilyGameScreen.mushroomY.get(j);
                boolean collisionX = (x[i] >= mx && x[i] < mx + UNIT_SIZE) || (x[i] + UNIT_SIZE > mx && x[i] + UNIT_SIZE <= mx + UNIT_SIZE);
                boolean collisionY = (y[i] >= my && y[i] < my + UNIT_SIZE) || (y[i] + UNIT_SIZE > my && y[i] + UNIT_SIZE <= my + UNIT_SIZE);

                if (collisionX && collisionY) {
                    // Mushroom hit, change direction and move down
                    y[i] += UNIT_SIZE;
                    velX[i] *= -1; // Change direction
                    break; // Exit the loop after collision with one mushroom
                }
            }

            // Check if the centipede goes out of bounds
            if (x[i] < 0 || x[i] >= WIDTH) {
                y[i] += UNIT_SIZE;
                velX[i] *= -1; // Reverse direction
            }
        }
    }

    public void shorten(int index) {
        if (index < 0 || index >= length) {
            return; // Invalid index, do nothing
        }
        length = index;
        int[] newX = new int[length];
        int[] newY = new int[length];
        int[] newVelX = new int[length];
        int[] newVelY = new int[length];
        System.arraycopy(x, 0, newX, 0, length);
        System.arraycopy(y, 0, newY, 0, length);
        System.arraycopy(velX, 0, newVelX, 0, length);
        System.arraycopy(velY, 0, newVelY, 0, length);
        x = newX;
        y = newY;
        velX = newVelX;
        velY = newVelY;
    }

    public void moveDownAndSwitchDirection() {
        for (int i = 0; i < length; i++) {
            y[i] += UNIT_SIZE; // Move down
            velX[i] *= -1; // Switch direction
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        LilyGameScreen.innerFrame.repaint();
    }
}