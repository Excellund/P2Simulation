package simulation.fields;

import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

import java.util.Objects;

public class Carcass implements Field {
    private int nutrition;
    private Vector position;

    public Carcass(int nutrition, Vector position) {
        this.nutrition = nutrition;
        this.position = position;
    }

    public int consume(int amount) {
        //tries to consume the specified amount of energy.
        //Returns the actual consumed amount of energy.

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
        //subtracts energy from the carcass.
        //Removes the carcass from the SimulationSpace
        //if no nutrients are left.

        nutrition -= Settings.CARCASS_DECAY_PER_TIMESTEP;

        if (!isAlive()) {
            space.queueRemoveField(this);
        }
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
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carcass carcass = (Carcass) o;
        return nutrition == carcass.nutrition &&
                Objects.equals(position, carcass.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nutrition, position);
    }
}
