package Simulation;

public class FishEgg implements Field {

    Color color;
    Vector position;
    FishGenome genome;
    int numEggs;
    int timeBeforeHatch;

    @Override
    public void update(SimulationSpace space) {

        //TODO: Take death of eggs into consideration
        if (timeBeforeHatch <= 0) {
            for (int i = 0; i < numEggs; i++) {
                space.addField(position, new Fish(genome, position));
            }
            numEggs = 0;
        }
    }

    @Override
    public void interact(Field field, SimulationSpace space) {
        return;
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
}
