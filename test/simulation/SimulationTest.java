package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimulationTest {

    Simulation simulation;

    @BeforeEach
    public void beforeEach() {
        simulation = new Simulation(750, 750);
    }
}
