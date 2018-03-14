package SimulationPackage;

import GFX.ColorRGB;
import GFX.Display;
import SimulationPackage.Entities.Field;
import VectorPackage.Vector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation implements Runnable {
    private boolean isRunning = true;
    private Display scene;
    private int framesPerSecond, movesPerFrame;
    private Timer t;
    private Random random = new Random();

    private ArrayList<Field> entities;


    public Simulation(Display scene, int framesPerSecond, int movesPerFrame) {
        this.scene = scene;
        this.framesPerSecond = framesPerSecond;
        this.movesPerFrame = movesPerFrame;

        entities = new ArrayList<>();

        for (int i = 0; i < 1000; ++i) {
            Field subject = new Field(new Vector(0, 0), 0, new ColorRGB(0, 0, 0));
            subject.radius = random.nextInt(15) + 2;
            subject.position.x = random.nextInt(scene.getWidth());
            subject.position.y = random.nextInt(scene.getHeight());
            subject.color.r = random.nextInt(120) + 20;
            subject.color.g = random.nextInt(235) + 20;
            subject.color.b = random.nextInt(120) + 20;
            entities.add(i, subject);
        }
    }

    public void moveEntities() {
        Iterator<Field> i1 = entities.iterator();
        while (i1.hasNext()) {
            Field entity = i1.next();
            int newX = random.nextInt(2) == 1 ? 1 : -1;
            int newY = random.nextInt(2) == 1 ? 1 : -1;

            entity.position.x += newX;
            entity.position.y += newY;

            Iterator<Field> i2 = entities.iterator();
            while (i2.hasNext()) {
                Field other = i2.next();
                if (entity != other && entity.isColliding(other) && entity.getRadius() <= other.getRadius()) {
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

        t = new Timer(sleepTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < movesPerFrame; ++i) {
                    moveEntities();
                }

                scene.drawFrame(entities);

                if (!isRunning) {
                    t.stop();
                }
            }
        });

        t.start();
    }

    public void stop() {
        isRunning = false;
        scene.close();
    }
}
