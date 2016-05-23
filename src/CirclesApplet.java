import javax.swing.JApplet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

public class CirclesApplet extends JApplet {

    private final int MAX_CIRCLES = 30;
    private final int SLEEP_DURATION = 50;

    private Random rand = new Random();
    private final int RES_X = 500;     // Window width
    private final int RES_Y = 400;      // Window height

    private class Circle {
        Color color;
        private final int RADIUS_MIN = 0;
        private final int RADIUS_RANGE = 100;
        int centerX, centerY;
        int radius;

        Circle() {
            color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); // (R, G, B)
            centerX = rand.nextInt(RES_X);
            centerY = rand.nextInt(RES_Y);
            radius = RADIUS_MIN + rand.nextInt(RADIUS_RANGE);
        }

        void expand() { radius++; }
    }

    private boolean animating = true;
    private ArrayList<Circle> circles;
    private Image offImage;
    private Graphics offG;

    @Override
    public void init() {
        circles = new ArrayList<>();
        circles.add(new Circle());
        setSize(RES_X, RES_Y);

        new Thread() {
            @Override
            public void run() {
                while (animating) {
                    updateAnimation();
                    repaint();
                    delayAnimation();
                }
            }
        }.start();
    }

    private void updateAnimation() {
        if (circles.size() < MAX_CIRCLES) {
            circles.add(new Circle());
        } else {
            circles.remove(0);
        }
        circles.forEach(Circle::expand);
    }

    private void delayAnimation() {
        try {
            Thread.sleep(SLEEP_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() { animating = false; }

    @Override
    public void paint(Graphics g) { update(g); }

    @Override
    public void update(Graphics g) {
        if (offImage == null) {
            offImage = createImage(getWidth(), getHeight());
            offG = offImage.getGraphics();
        }

        // Smooth out circles' edges
        Graphics2D g2d = (Graphics2D) offG;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render circles
        for (Circle c : circles) {
            offG.setColor(c.color);
            offG.fillOval(c.centerX - c.radius,
                       c.centerY - c.radius,
                       2 * c.radius, 2 * c.radius);
        }
        g.drawImage(offImage, 0, 0, this);
    }
}