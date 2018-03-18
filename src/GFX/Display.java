package GFX;

import SimulationPackage.Entities.Field;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Display {
    // Fields:
    private boolean isRunning;
    private JFrame frame;
    private GFX.Canvas canvas;
    private int width, height;
    private String title;

    // Constructor:
    public Display(String title, int width, int height) {
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
    public int drawFrame(ArrayList<Field> subjects) {
        int count = 0;

        canvas.clear(new Color(0, 0 ,0));

        for (Field subject : subjects) {
            canvas.drawField(subject);

            // For testing
            count++;
        }

        Graphics gfx = canvas.getGFX();
        gfx.setColor(Color.CYAN);
        gfx.drawString("Number: " + subjects.size(), 10, 10);
        canvas.repaint();

        return count;
    }

    public void close() {
        frame.setVisible(!isRunning);
        frame.dispose();
    }

    // Getters:
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isRunning(){
        return isRunning;
    }
}
