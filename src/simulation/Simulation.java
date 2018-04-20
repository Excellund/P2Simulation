package simulation;

import utils.CountingRandom;
import utils.Vector;

import java.util.ListIterator;
import java.util.Random;

public class Simulation {
    // Fields
    private Random random = CountingRandom.getInstance();
    private int width;
    private int height;
    SimulationSpace space;

    // Constants
    public final static int NUM_INITIAL_SUBJECTS = 1200;
    public final static int PLANKTON_GROWTH_PER_MOVE = 300;
    public final static int FISH_HEALTH_CONSUMPTION = 300;

    // Constructor:
    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;

        space = new SimulationSpace(width, height);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                space.getTile(x, y).addDensity(random.nextInt(200000));
            }
        }

        for (int i = 0; i < NUM_INITIAL_SUBJECTS; ++i) {
            addSubject();
        }
    }

    // Methods:
    private void addSubject() {
        int posX = random.nextInt(width);
        int posY = random.nextInt(height);
        Field subject = new Fish(new FishGenome(), new Vector(posX, posY));

        space.addField(subject.getPosition(), subject);
    }

    private void moveSubjects() {
        ListIterator<Field> iterator = space.listIterator();

        while (iterator.hasNext()) {
            Field subject = iterator.next();

            subject.update(space);

            if (!subject.isAlive()) {
                iterator.remove();
            }
        }
    }

    private long tileDensity(int xLower, int xHigher, int yLower, int yHigher, Tile[][] tiles) {
        long combined = 0;

        for (int y = yLower; y < yHigher; ++y) {
            for (int x = xLower; x < xHigher; ++x) {
                combined += tiles[y][x].getMuDensity();
            }
        }

        return combined;
    }

    private void sustainPlankton() {
        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                space.getTile(x, y).addDensity(PLANKTON_GROWTH_PER_MOVE);
            }
        }
    }

    private void sustainFields() {
        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                if (space.getTile(x, y).getSubjects().size() >= 2) {
                    int count = 0;

                    ListIterator<Field> iterator = space.getTile(x, y).getSubjects().listIterator();

                    while (iterator.hasNext()) {
                        Field subject = iterator.next();

                        if (subject instanceof Fish) {
                            Fish fishSubject = (Fish) subject;

                            if (fishSubject.getHealth() >= 250) {
                                fishSubject.subtractHealth(FISH_HEALTH_CONSUMPTION);
                                ++count;
                            }

                            if (count == 2) {
                                addSubject();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void timeStep() {
        moveSubjects();
        sustainPlankton();
        sustainFields();
    }


    // Getters:
    public SimulationSpace getSpace() {
        return space;
    }
}
