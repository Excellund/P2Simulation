package simulation;

import org.junit.jupiter.api.*;
import simulation.fields.Field;
import simulation.fields.Fish;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationSpaceTest {

    Field field;
    SimulationSpace simulationSpace;

    @BeforeEach
    public void beforeEach() {
        field = new Fish(new FishGenome(), new Vector(0,0 ));
        simulationSpace = new SimulationSpace(750, 750);
    }

    @Test
    public void getTileClass() {
        assertEquals(Tile.class, simulationSpace.getTile(0, 0).getClass());
    }

    @Test
    public void addField() {
        simulationSpace.addField(field);
        assertTrue(simulationSpace.getActiveSubjects().contains(field));
    }

    @Test
    public void removeField() {
        simulationSpace.addField(field);
        simulationSpace.removeField(field);
        assertFalse(simulationSpace.getActiveSubjects().contains(field));
    }


}
