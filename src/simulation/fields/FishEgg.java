package simulation.fields;

import simulation.FishGenome;
import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

public class FishEgg implements Field {
    Color color;
    Vector position;
    FishGenome genome;
    int numEggs;
    int timeBeforeHatch;

    public FishEgg(Vector position, int numEggs, int timeBeforeHatch, FishGenome genome) {
        this.position = position;
        this.numEggs = numEggs;
        this.timeBeforeHatch = timeBeforeHatch;
        this.genome = genome;
    }

    @Override
    public void update(SimulationSpace space) {
        if (timeBeforeHatch <= 0) {
            for (int i = 0; i < numEggs; i++) {
                space.addField(new Fish(genome, position));
            }
            numEggs = 0;
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
