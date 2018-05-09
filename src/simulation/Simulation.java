package simulation;

import simulation.fields.Field;
import simulation.fields.Fish;
import utils.CountingRandom;
import utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Simulation {
    // Fields
    private Random random = CountingRandom.getInstance();
    private int width;
    private int height;
    private SimulationSpace space;
    private List<Vessel> vessels;

    // Constructor:
    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;

        space = new SimulationSpace(width, height);
        vessels = new ArrayList<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                space.getTile(x, y).addDensity(random.nextInt((int) Settings.INITIAL_MAX_PLANKTON_DENSITY));
            }
        }

        FishGenome initialGenome = new FishGenome();

        for (int i = 0; i < Settings.NUM_INITIAL_SUBJECTS; ++i) {
            int posX = random.nextInt(width);
            int posY = random.nextInt(height);

            FishGenome genome = new FishGenome(initialGenome);
            genome.mutate();
            Field subject = new Fish(genome, new Vector(posX, posY));

            space.addField(subject);

            if (i % 300 == 0) {
                initialGenome = new FishGenome();
            }
        }

        for (int i = 0; i < Settings.NUM_VESSELS; ++i) {
            vessels.add(new Vessel(new Vector(width, height)));
        }
    }

    private void updateFields() {
        for (Field subject : space) {
            subject.update(space);
        }
    }

    private void updatePlankton() {
        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                Tile current = space.getTile(x, y);
                current.addDensity(calculateTilePlanktonGrowth(current, new Vector(x, y)));
                current.addDensity((int) (Settings.PLANKTON_GROWTH_PER_TIMESTEP / 400));
            }
        }
    }

    private int calculateTilePlanktonGrowth(Tile tile, Vector position) {
        Vector min = new Vector(position.x - 1, position.y - 1);
        Vector max = new Vector(position.x + 1, position.y + 1);
        float sum = 0;

        for (int y = min.y; y <= max.y; ++y) {
            if (y >= 0 && y < height) {
                for (int x = min.x; x <= max.x; ++x) {
                    if (x >= 0 && x < width) {
                        sum += space.getTile(x, y).getMuDensity() / 1000000f; //1000000 as in the max µ density
                    }
                }
            }
        }

        return (int) (Settings.PLANKTON_GROWTH_PER_TIMESTEP * (sum / 9));
    }

    private void updateVessels() {
        ListIterator<Vessel> iterator = vessels.listIterator();

        while (iterator.hasNext()) {
            Vessel vessel = iterator.next();

            vessel.timeStep(space);

            if (vessel.quotaIsSpent()) {
                iterator.remove();
            }
        }

        for (int i = 0; i < Settings.NUM_VESSELS - vessels.size(); ++i) {
            vessels.add(new Vessel(new Vector(width, height)));
        }
    }

    public void timeStep() {
        updateFields();
        updatePlankton();
        updateVessels();
        space.processQueue();
    }

    // Getters:
    public SimulationSpace getSpace() {
        return space;
    }

    public List<Vessel> getVessels() {
        return vessels;
    }
}
