import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Display {
    private JFrame frame;
    private Canvas canvas;
    private int width, height;
    private String title;

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

    public void drawFrame(ArrayList<Field> subjects) {
        canvas.clear(new ColorRGB(0, 0, 0));

        for (Field subject : subjects) {
            canvas.drawField(subject);
        }

        Graphics g = canvas.getG();
        g.setColor(Color.CYAN);
        g.drawString("Number: " + subjects.size(), 10, 10);
        canvas.repaint();
    }

    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    //access private fields

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
