package simulation;

public class EvolutionSimulation {

    SimulationSpace space;
    private int width;
    private int height;

    EvolutionSimulation(int width, int height) {
        this.width = width;
        this.height = height;
        space = new SimulationSpace(width, height);
    }

    public void timeStep() {
    }

}
