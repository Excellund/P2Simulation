package Simulation;

public interface Field {
    default void update(SimulationSpace space) {
        return;
    }
    default void interact(Field field, SimulationSpace space) {
        return;
    }
    boolean isAlive(); //Should the element still exist?

    // Getters:
    Vector getPosition();
    Color getColor();

    // Setters:
    void setPosition(Vector position);
}
