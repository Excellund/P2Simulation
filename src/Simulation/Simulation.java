package Simulation;

import utils.CountingRandom;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class Simulation {
    // Fields
    private Random random = CountingRandom.getInstance();
    private Tile[][] tiles;
    private ArrayList<Field> activeSubjects;
    private int width;
    private int height;

    // Constants
    public final static int NUM_INITIAL_SUBJECTS = 1200;
    public final static int PLANKTON_GROWTH_PER_MOVE = 300;
    public final static int FISH_HEALTH_CONSUMPTION = 300;

    // Constructor:
    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[height][width];
        activeSubjects = new ArrayList<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                tiles[y][x] = new Tile(random.nextInt(200000));
            }
        }

        for (int i = 0; i < NUM_INITIAL_SUBJECTS; ++i) {
            addSubject();
        }
    }

    // Methods:
    private void addSubject() {
        Field subject = new Field(new Vector(0, 0), 0, new Color(0, 0, 0), random.nextInt(100) + 50);
        subject.radius = random.nextInt(15) + 2;
        subject.position.x = random.nextInt(width);
        subject.position.y = random.nextInt(height);
        int r = random.nextInt(255);
        int b = random.nextInt(255);
        subject.color = new Color(r, 0, b);

        tiles[subject.position.y][subject.position.x].addSubject(subject);
        activeSubjects.add(subject);
    }

    private void moveSubjects() {
        ListIterator<Field> iterator = activeSubjects.listIterator();

        while (iterator.hasNext()) {
            Field subject = iterator.next();

            Vector previous = subject.getPosition();

            tiles[subject.position.y][subject.position.x].removeSubject(subject);
            subject.setPosition(favoredMove(subject));

            if ((subject.position.x < 0 || subject.position.x >= width) ||
                    (subject.position.y < 0 || subject.position.y >= height)) {
                subject.setPosition(previous);
            }

            tiles[subject.position.y][subject.position.x].addSubject(subject);

            if (tiles[subject.position.y][subject.position.x].getMuDensity() < 100000) {
                subject.subtractHealth(3);

                if (subject.getHealth() == 0) {
                    tiles[subject.position.y][subject.position.x].removeSubject(subject);
                    iterator.remove();
                }
            } else {
                subject.addHealth(1);
            }

            tiles[subject.position.y][subject.position.x].subtractDensity(100000);
        }
    }

    private Vector favoredMove(Field entity) { //TODO: revise
        int xLower, xHigher, yLower, yHigher, x, y, xOffset, yOffset;
        long last, current;

        last = 0;
        x = entity.position.x;
        y = entity.position.y;

        if (entity.getHealth() >= 290) {
            xLower = entity.position.x < 3 ? 0 : entity.position.x - 3;
            xHigher = entity.position.x > width - 4 ? width - 1 : entity.position.x + 3;

            yLower = entity.position.y < 3 ? 0 : entity.position.y - 3;
            yHigher = entity.position.y > height - 4 ? height - 1 : entity.position.y + 3;

            last = tiles[entity.position.y][entity.position.x].getSubjects().size();

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i) {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j) {
                    int ic = i > yHigher ? i - yOffset : i;
                    int jc = j > xHigher ? j - xOffset : j;

                    if ((ic != entity.position.y || jc != entity.position.x) && tiles[ic][jc].getSubjects().size() > last) {
                        last = tiles[ic][jc].getSubjects().size();
                        x = (jc - x) != 0 ? (jc - x) / Math.abs(jc - x) + x : x;
                        y = (ic - y) != 0 ? (ic - y) / Math.abs(ic - y) + y : y;
                    }
                }
            }
        }

        if (entity.getHealth() < 290 || last < 1) {
            xLower = entity.position.x < 1 ? 0 : entity.position.x - 1;
            xHigher = entity.position.x > width - 2 ? width - 1 : entity.position.x + 1;

            yLower = entity.position.y < 1 ? 0 : entity.position.y - 1;
            yHigher = entity.position.y > height - 2 ? height - 1 : entity.position.y + 1;

            last = tiles[entity.position.y][entity.position.x].getMuDensity();

            x = entity.position.x;
            y = entity.position.y;

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i) {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j) {
                    int ic = i > yHigher ? i - 3 : i;
                    int jc = j > xHigher ? j - 3 : j;

                    if (tiles[ic][jc].getMuDensity() > last) {
                        last = tiles[ic][jc].getMuDensity();
                        x = jc;
                        y = ic;
                    }
                }
            }

            if (last < 100000) {
                xLower = entity.position.x < 3 ? 0 : entity.position.x - 3;
                xHigher = entity.position.x > width - 4 ? width - 1 : entity.position.x + 3;

                yLower = entity.position.y < 3 ? 0 : entity.position.y - 3;
                yHigher = entity.position.y > height - 4 ? height - 1 : entity.position.y + 3;

                last = tileDensity(xLower, entity.position.x, yLower, entity.position.y, tiles);
                x = -1;
                y = -1;

                current = tileDensity(entity.position.x, xHigher, yLower, entity.position.y, tiles);

                if (current > last) {
                    x = 1;
                    y = -1;
                    last = current;
                }

                current = tileDensity(entity.position.x, xHigher, entity.position.y, yHigher, tiles);

                if (current > last) {
                    x = 1;
                    y = 1;
                    last = current;
                }

                current = tileDensity(xLower, entity.position.x, entity.position.y, yHigher, tiles);

                if (current > last) {
                    x = -1;
                    y = 1;
                    last = current;
                }

                if (last < 4000000) {
                    x = random.nextInt(2) == 1 ? 1 : -1;
                    y = random.nextInt(2) == 1 ? 1 : -1;
                }

                x += entity.position.x;
                y += entity.position.y;
            }
        }

        return new Vector(x, y);
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
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[0].length; ++x) {
                tiles[y][x].addDensity(PLANKTON_GROWTH_PER_MOVE);
            }
        }
    }

    private void sustainFields() {
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[0].length; ++x) {
                if (tiles[y][x].getSubjects().size() >= 2) {
                    int count = 0;

                    ListIterator<Field> iterator = tiles[y][x].getSubjects().listIterator();

                    while (iterator.hasNext()) {
                        Field subject = iterator.next();

                        if (subject.getHealth() >= 250) {
                            subject.subtractHealth(FISH_HEALTH_CONSUMPTION);
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

    public void timeStep() {
        moveSubjects();
        sustainPlankton();
        sustainFields();
    }


    // Getters:

    public final Tile[][] getTiles() {
        return tiles;
    }

}
