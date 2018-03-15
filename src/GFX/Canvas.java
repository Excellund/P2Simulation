package GFX;

import SimulationPackage.Entities.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

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
    public void paintComponent(Graphics gfx) {
        gfx.drawImage(scene, 0, 0, null);
    }

    public void clear(Color color) {
        graphics.setColor(color);
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawField(Field subject) {
        graphics.setColor(subject.color);
        graphics.fillOval(subject.position.x - subject.radius, subject.position.y - subject.radius, subject.radius * 2, subject.radius * 2);
    }

    // Getters:
    public BufferedImage getScene() {
        return scene;
    }

    public Graphics getGFX() {
        return graphics;
    }
}
