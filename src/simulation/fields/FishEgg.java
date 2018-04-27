package simulation.fields;

import simulation.FishGenome;
import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.Random;

public class FishEgg implements Field {
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
        if (timeBeforeHatch <= 0 && numEggs > 0) {
            Random r = CountingRandom.getInstance();
            int hatches = r.nextInt(numEggs) / 4;

            for (int i = 0; i < hatches; i++) {
                numEggs -= 1;

                FishGenome mutatedGenome = new FishGenome(genome);

                mutatedGenome.mutate();
                space.queueAddField(new Fish(mutatedGenome, position));
            }

            numEggs = 0;
        } else {
            --timeBeforeHatch;
        }

        if (!isAlive()) {
            space.queueRemoveField(this);
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
        return new Color(255, 255, 255);
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
