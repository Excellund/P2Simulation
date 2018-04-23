package simulation;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimulationTest {

    Simulation simulation;

    @BeforeEach
    public void beforeEach() {
        simulation = new Simulation(750, 750);
    }
}
