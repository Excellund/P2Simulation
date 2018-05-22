package simulation;

import simulation.fields.Field;
import simulation.fields.Fish;
import utils.CountingRandom;
import utils.Vector;

import java.util.*;

public class Simulation {
    // Fields
    private Random random = CountingRandom.getInstance();
    private SimulationSpace space;
    private List<Vessel> vessels;
    private long currentTimeStep = 0;

    // Constructor:
    public Simulation(int width, int height) {
        initialize(width, height);
    }

    public void restart() {
        //stops the current instance of the simulation and starts a new run
        CountingRandom.getInstance().setState(System.nanoTime(), 0);
        initialize(space.getWidth(), space.getHeight());
    }

    private void initialize(int width, int height) {
        //initializes the simulation by creating the SimulationSpace
        //and filling it with the appropriate vessels and fish
        space = new SimulationSpace(width, height);
        vessels = new ArrayList<>();
        currentTimeStep = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                space.getTile(x, y).addDensity(random.nextInt((int) Settings.INITIAL_MAX_PLANKTON_DENSITY));
            }
        }

        FishGenome initialGenome = new FishGenome();

        for (int i = 0; i < Settings.NUM_INITIAL_FISH; ++i) {
            int posX = random.nextInt(width);
            int posY = random.nextInt(height);

            FishGenome genome = new FishGenome(initialGenome);
            genome.mutate();
            Fish fish = new Fish(genome, new Vector(posX, posY));
            fish.addEnergy(500);

            space.addField(fish);

            if (i % 500 == 0) {
                initialGenome = new FishGenome(); //create a new fish genome for every 500 fish
            }
        }

        for (int i = 0; i < Settings.NUM_VESSELS; ++i) {
            vessels.add(new Vessel(new Vector(width, height)));
        }

        currentTimeStep = 0;
    }

    private void updateFields() {
        //calls the update method for each Field in the SimulationSpace.
        //Update performs the actions the given Field should carry out every timestep.
        for (Field field : space) {
            field.update(space);
        }
    }

    private void updatePlankton() {
        //Increases the plankton density in the SimulationSpace.
        //Each tile has its own corresponding density, which will be increased
        //based on the density of its neighbors.
        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                Tile current = space.getTile(x, y);
                if (current.getMuDensity() < Settings.MAX_PLANKTON) {
                    current.addDensity(calculateTilePlanktonGrowth(new Vector(x, y)));
                    current.addDensity((int) (Settings.PLANKTON_GROWTH_PER_TIMESTEP / 400));
                }
            }
        }
    }

    private int calculateTilePlanktonGrowth(Vector position) {
        //returns an integer representing the amount of mu density
        //the Tile at the specified position should grow on a timestep,
        //based on its neighbors.
        Vector min = new Vector(position.x - 1, position.y - 1);
        Vector max = new Vector(position.x + 1, position.y + 1);
        float sum = 0;

        for (int y = min.y; y <= max.y; ++y) {
            if (y >= 0 && y < space.getHeight()) {
                for (int x = min.x; x <= max.x; ++x) {
                    if (x >= 0 && x < space.getWidth()) {
                        sum += space.getTile(x, y).getMuDensity(); //1000000 as in the max µ density
                    }
                }
            }
        }

        return (int) (Settings.PLANKTON_GROWTH_PER_TIMESTEP * (sum / 1000000f / 9f));
    }

    private void updateVessels() {
        //maintains the population of vessels in the Simulation.
        //If a vessel has used its quota, it will be replaced by a new vessel.
        //Otherwise, the timestep method is called, which carries out procedures
        //a vessel should perform each timestep.
        ListIterator<Vessel> iterator = vessels.listIterator();

        while (iterator.hasNext()) {
            Vessel vessel = iterator.next();

            vessel.timeStep(space);

            if (vessel.quotaIsSpent()) {
                iterator.remove();
            }
        }

        for (int i = 0; i < Settings.NUM_VESSELS - vessels.size(); ++i) {
            vessels.add(new Vessel(new Vector(space.getWidth(), space.getHeight())));
        }
    }

    public void timeStep() {
        //calls all the update methods that carry out procedures
        //necessary to each timestep.
        updateFields();
        updatePlankton();
        updateVessels();
        space.processQueue();

        currentTimeStep++;
    }

    // Getters:
    public SimulationSpace getSpace() {
        return space;
    }

    public List<Vessel> getVessels() {
        return vessels;
    }

    public long getCurrentTimeStep() {
        return currentTimeStep;
    }

    public void applySnapshot(Snapshot snapshot) {
        //overwrites the current instance of the Simulation
        //with the specified snapshot,
        //replacing the SimulationSpace and vessels.
        CountingRandom.getInstance().setState(snapshot.getRandomSeed(), snapshot.getRandomCounter());
        currentTimeStep = snapshot.getCurrentTimeStep();

        space = new SimulationSpace(space.getWidth(), space.getHeight());
        space.applySnapshot(snapshot);

        vessels = new ArrayList<>();
        Collections.addAll(vessels, snapshot.getVessels());
    }
}
