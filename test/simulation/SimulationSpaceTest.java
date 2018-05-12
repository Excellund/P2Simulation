package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulation.fields.Field;
import simulation.fields.Fish;
import utils.Vector;

public class SimulationSpaceTest {

    Field field;
    SimulationSpace simulationSpace;

    @BeforeEach
    public void beforeEach() {
        field = new Fish(new FishGenome(), new Vector(0, 0));
        simulationSpace = new SimulationSpace(750, 750);
    }

    @Test
    public void addField() {

    }
}
