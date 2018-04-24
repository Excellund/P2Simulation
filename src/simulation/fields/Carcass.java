package simulation.fields;

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

    public void consume(int amount) {
        nutrition -= amount;
    }

    public int getNutrition() {
        return nutrition;
    }

    @Override
    public void update(SimulationSpace space) {
        return;
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
        return null;
    }

    @Override
    public void setPosition(Vector position) { }
}
