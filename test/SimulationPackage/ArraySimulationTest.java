package SimulationPackage;

import GFX.Display;
import org.junit.jupiter.api.*;

public class ArraySimulationTest {

    ArraySimulation sim;
    Display scene;

    @BeforeEach
    public void beforeEach() {
        scene = new Display("Test", 750, 750);
        sim = new ArraySimulation(scene, 60, 2);
    }

    @Test
    public void test() {

    }
}
