import GFX.Display;
import SimulationPackage.ArraySimulation;
import SimulationPackage.Simulation;

public class Main {

    public static final int MOVES_PER_FRAME = 20;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int DISPLAY_DIMENSIONS = 300;

    public static void main(String[] args) {
        Display display = new Display("Test", DISPLAY_DIMENSIONS, DISPLAY_DIMENSIONS);
        ArraySimulation sim = new ArraySimulation(display, FRAMES_PER_SECOND, MOVES_PER_FRAME);

        Thread thread = new Thread(sim);
        thread.start();
        //sim.stop();
    }

    // Getters:
    public static int getMovesPerFrame() {
        return MOVES_PER_FRAME;
    }
}
