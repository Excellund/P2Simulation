package GFX;

import SimulationPackage.Entities.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    // Fields:
    private BufferedImage scene;
    private Graphics graphics;

    // Constructor:
    public Canvas(int width, int height) {
        scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = scene.getGraphics();
    }

    // Methods:
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

    // Getters:
    public BufferedImage getScene() {
        return scene;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    // Setters:
    public void setScene(BufferedImage scene) {
        this.scene = scene;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }
}
