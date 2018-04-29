package simulation.fields;

import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

public class Carcass implements Field {
    private int nutrition;
    private Vector position;

    public Carcass(int nutrition, Vector position) {
        this.nutrition = nutrition;
        this.position = position;
    }

    public int consume(int amount) {
        if (amount > nutrition) {
            int energy = nutrition;

            nutrition = 0;

            return energy;
        }

        nutrition -= amount;

        return amount;
    }

    public int getNutrition() {
        return nutrition;
    }

    @Override
    public void update(SimulationSpace space) {
        if (!isAlive()) {
            space.queueRemoveField(this);
        }

        nutrition -= Settings.CARCASS_DECAY_PER_TIMESTEP;
    }

    @Override
    public boolean isAlive() {
        return nutrition > 0;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        return new Color(0, 0, 155);
    }

    @Override
    public void setPosition(Vector position) {

    }
}
