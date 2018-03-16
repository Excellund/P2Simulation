package SimulationPackage;

import GFX.Display;
import SimulationPackage.Entities.Field;
import VectorPackage.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation implements Runnable {
    // Fields:
    private boolean isRunning;
    private Display scene;
    private int framesPerSecond, movesPerFrame;
    private Timer t;
    private Random random = new Random();

    private ArrayList<Field> subjects;

    // Constructor:
    public Simulation(Display scene, int framesPerSecond, int movesPerFrame) {
        isRunning = true;
        this.scene = scene;
        this.framesPerSecond = framesPerSecond;
        this.movesPerFrame = movesPerFrame;

        subjects = new ArrayList<>();

        int populationInit = 1000;

        for (int i = 0; i < populationInit; ++i) {
            Field subject = new Field(new Vector(0, 0), 0, new Color(0, 0, 0));
            subject.radius = random.nextInt(15) + 2;
            subject.position.x = random.nextInt(scene.getWidth());
            subject.position.y = random.nextInt(scene.getHeight());
            int r = random.nextInt(120) + 20;
            int g = random.nextInt(235) + 20;
            int b = random.nextInt(120) + 20;
            subject.color = new Color(r, g, b);
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

    // Getters:
    public Field getSubject(int index) {
        return this.subjects.get(index);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Timer getT() {
        return t;
    }
}
