package SimulationPackage.Entities;

import GFX.Display;
import SimulationPackage.Simulation;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    Simulation simulation;
    Display display;

    @BeforeEach
    public void beforeEach() {
        display = new Display("Test", 1920, 1080);
        simulation = new Simulation(display, 60, 2);
    }

    @Test
    public void moveSubjectPositionChanged() {
        int previous = simulation.getSubject(0).position.x;
        simulation.moveSubjects();
        assertTrue(Math.abs(previous - simulation.getSubject(0).position.x) == 1);
    }

    @Test
    public void stop() {
        simulation.stop();
        assertFalse(simulation.isRunning());
    }

    @Test
    public void run() {
        simulation.run();
        assertTrue(simulation.getT().isRunning());
    }

    @Test
    public void runSceneStopped() {
        simulation.stop();
        // TODO: Make a custom exception for this.
        assertThrows(NullPointerException.class, () -> {
            simulation.getT().isRunning();
        });
    }
}
