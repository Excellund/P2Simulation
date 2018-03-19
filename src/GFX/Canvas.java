package GFX;

import SimulationPackage.Entities.Field;
import SimulationPackage.Entities.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Canvas extends JPanel
{
    // Fields:
    private BufferedImage scene;
    private Graphics graphics;

    // Constructor:
    public Canvas(int width, int height)
    {
        scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = scene.getGraphics();
    }

    // Methods:
    public void paintComponent(Graphics gfx)
    {
        gfx.drawImage(scene, 0, 0, null);
    }

    public void clear(Color color)
    {
        graphics.setColor(color);
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawField(Field subject)
    {
        scene.setRGB(subject.position.x, subject.position.y, subject.color.getRGB());
    }

    public void drawTiles(Tile[][] tiles)
    {
        for (int y = 0; y < tiles.length; ++y)
        {
            for (int x = 0; x < tiles[0].length; ++x)
            {
                double green = (((double) tiles[y][x].getMuDensity() / 1000000) * 80);
                green = Math.pow(green / 255.0, 0.5)*255; //Gamma correction
                scene.setRGB(x, y, (int) green << 8);
            }
        }
    }

    // Getters:
    public BufferedImage getScene()
    {
        return scene;
    }

    public Graphics getGFX()
    {
        return graphics;
    }
}
