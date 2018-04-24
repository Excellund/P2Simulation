package simulation.fields;

import simulation.FishGenome;
import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

public class FishEgg implements Field {
    Color color;
    Vector position;
    FishGenome genome;
    int numEggs;
    int timeBeforeHatch;

    public FishEgg(Vector position, FishGenome genome, int numEggs) {
        this.position = position;
        this.genome = genome;
        this.numEggs = numEggs;

        timeBeforeHatch = (int) Settings.TIME_BEFORE_HATCH;
    }

    public FishEgg(Vector position, int numEggs, int timeBeforeHatch, FishGenome genome) {
        this.position = position;
        this.genome = genome;
        this.numEggs = numEggs;
        this.timeBeforeHatch = timeBeforeHatch;
    }

    @Override
    public void update(SimulationSpace space) {
        if (!isAlive()) {
            space.queueRemoveField(this);
        }

        if (timeBeforeHatch <= 0) {
            for (int i = 0; i < numEggs; i++) {
                FishGenome mutatedGenome = new FishGenome(genome);

                mutatedGenome.mutate();
                space.queueAddField(new Fish(mutatedGenome, position));
            }

            numEggs = 0;
        } else {
            --timeBeforeHatch;
        }
    }

    public void subtractEggs(int amount) {
        numEggs -= amount;
    }

    @Override
    public boolean isAlive() {
        return numEggs > 0;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setPosition(Vector position) {
        this.position = position;
    }

    public FishGenome getGenome() {
        return genome;
    }

    public int getNumEggs() {
        return numEggs;
    }

    public int getTimeBeforeHatch() {
        return timeBeforeHatch;
    }
}
