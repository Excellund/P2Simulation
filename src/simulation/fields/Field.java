package simulation.fields;

import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

public interface Field {
    default void update(SimulationSpace space) {
        return;
    }

    boolean isAlive(); //Should the element still exist?

    // Getters:
    Vector getPosition();

    Color getColor();

    // Setters:
    void setPosition(Vector position);
}
