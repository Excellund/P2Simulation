package simulation;

import simulation.fields.Field;
import simulation.fields.Fish;
import utils.CountingRandom;
import utils.Vector;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class Simulation {
    // Fields
    private Random random = CountingRandom.getInstance();
    private int width;
    private int height;
    private SimulationSpace space;
    private ArrayList<Vessel> vessels;

    // Constructor:
    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;

        space = new SimulationSpace(width, height);
        vessels = new ArrayList<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                space.getTile(x, y).addDensity(random.nextInt(200000));
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
        }

        for (int i = 0; i < Settings.NUM_VESSELS; ++i) {
            vessels.add(new Vessel(new Vector(width, height)));
        }
    }

    private void updateFields() {
        ListIterator<Field> iterator = space.listIterator();

        while (iterator.hasNext()) {
            Field subject = iterator.next();

            if (subject.isAlive()) {
                subject.update(space);
            } else {
                iterator.remove();
            }
        }
    }

    private void updatePlankton() {
        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                space.getTile(x, y).addDensity((int) Settings.PLANKTON_GROWTH_PER_TIMESTEP);
            }
        }
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

    public ArrayList<Vessel> getVessels() {
        return vessels;
    }
}
