package simulation.fields;

import simulation.FishGenome;
import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.Objects;
import java.util.Random;

public class FishEgg implements Field {
    private Vector position;
    private FishGenome genome;
    private int numEggs;
    private int timeBeforeHatch;

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
        //reduces the hatching timer
        //if it is 0, each egg has a chance to hatch
        //removes the object if no eggs remain
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

    public int subtractEggs(int amount) {
        //tries to subtract the specified amount of eggs
        //returns the actual amount of subtracted eggs
        if (amount > numEggs) {
            int eggs = numEggs;

            numEggs = 0;

            return eggs;
        }

        numEggs -= amount;

        return amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishEgg fishEgg = (FishEgg) o;
        return numEggs == fishEgg.numEggs &&
                timeBeforeHatch == fishEgg.timeBeforeHatch &&
                Objects.equals(position, fishEgg.position) &&
                Objects.equals(genome, fishEgg.genome);
    }

    @Override
    public int hashCode() {

        return Objects.hash(position, genome, numEggs, timeBeforeHatch);
    }
}
