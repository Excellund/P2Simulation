import javax.swing.*;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    private BufferedImage scene;
    private Graphics graphics;

    public Canvas(int width, int height) {
        scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = scene.getGraphics();
    }

    public void paintComponent(Graphics g) {
        g.drawImage(scene, 0, 0, null);
    }

    public void clear(ColorRGB color) {
        graphics.setColor(new Color(color.r, color.g, color.b));
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawField(Field subject) {
        graphics.setColor(new Color(subject.color.r, subject.color.g, subject.color.b));
        graphics.fillOval(subject.position.x - subject.radius, subject.position.y - subject.radius, subject.radius * 2, subject.radius * 2);
    }

    public Graphics getG() {
        return graphics;
    }
}
