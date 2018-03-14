package SimulationPackage;

import GFX.ColorRGB;
import GFX.Display;
import SimulationPackage.Entities.Field;
import VectorPackage.Vector;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation implements Runnable {
    // Fields:
    private boolean isRunning = true;
    private Display scene;
    private int framesPerSecond, movesPerFrame;
    private Timer t;
    private Random random = new Random();

    private ArrayList<Field> subjects;

    // Constructor:
    public Simulation(Display scene, int framesPerSecond, int movesPerFrame) {
        this.scene = scene;
        this.framesPerSecond = framesPerSecond;
        this.movesPerFrame = movesPerFrame;

        subjects = new ArrayList<>();

        for (int i = 0; i < 1000; ++i) {
            Field subject = new Field(new Vector(0, 0), 0, new ColorRGB(0, 0, 0));
            subject.radius = random.nextInt(15) + 2;
            subject.position.x = random.nextInt(scene.getWidth());
            subject.position.y = random.nextInt(scene.getHeight());
            subject.color.r = random.nextInt(120) + 20;
            subject.color.g = random.nextInt(235) + 20;
            subject.color.b = random.nextInt(120) + 20;
            subjects.add(i, subject);
        }
    }

    // Methods:
    public void moveSubjects() {
        Iterator<Field> i1 = subjects.iterator();
        while (i1.hasNext()) {
            Field subject = i1.next();
            int newX = random.nextInt(2) == 1 ? 1 : -1;
            int newY = random.nextInt(2) == 1 ? 1 : -1;

            subject.position.x += newX;
            subject.position.y += newY;

            Iterator<Field> i2 = subjects.iterator();
            while (i2.hasNext()) {
                Field other = i2.next();
                if (subject != other && subject.isColliding(other) && subject.getRadius() <= other.getRadius()) {
                    other.setRadius(other.getRadius() + 1);
                    i1.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        int sleepTime = 1000 / framesPerSecond;

        t = new Timer(sleepTime, e -> {
            for (int i = 0; i < movesPerFrame; ++i) {
                moveSubjects();
            }

            scene.drawFrame(subjects);

            if (!isRunning) {
                t.stop();
            }
        });

        t.start();
    }

    public void stop() {
        isRunning = false;
        scene.close();
    }
}
