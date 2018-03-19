import GFX.Display;
import SimulationPackage.ArraySimulation;
import SimulationPackage.Simulation;

public class Main
{
    public static void main(String[] args)
    {
        Display display = new Display("Test", 1000, 1000);
        ArraySimulation sim = new ArraySimulation(display, 60, 2);

        Thread thread = new Thread(sim);
        thread.start();
        //sim.stop();
    }
}
