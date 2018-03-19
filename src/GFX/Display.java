package GFX;

import SimulationPackage.Entities.Field;
import SimulationPackage.Entities.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Display
{
    // Fields:
    private JFrame frame;
    private GFX.Canvas canvas;
    private int width, height;
    private String title;

    // Constructor:
    public Display(String title, int width, int height)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        frame = new JFrame();
        frame.setTitle(this.title);
        frame.setSize(this.width, this.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new Canvas(this.width, this.height);
        frame.add(canvas);
        frame.setVisible(true);


    }

    // Methods:
    public void drawFrame(ArrayList<Field> subjects, Tile[][] tiles, int[] graphPoints, int size)
    {
        canvas.drawTiles(tiles);

        for (Field subject : subjects)
        {
            canvas.drawField(subject);
        }

        Graphics gfx = canvas.getGFX();
        gfx.setColor(Color.CYAN);
        gfx.drawString("Number: " + subjects.size(), 10, 10);

        for (int i = 0; i < size; i++) {
            int y = (-graphPoints[i] / 10 + this.height / 7 * 6);
            y = y < 0 ? 0 : y;
            y = y >= this.height - 1 ? this.height - 1 : y;

            canvas.getScene().setRGB(i, y, 0xFF0000);
        }

        canvas.repaint();
    }

    public void close()
    {
        frame.setVisible(false);
        frame.dispose();
    }

    // Getters:
    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public String getTitle()
    {
        return title;
    }

    // Setters:
    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
