package simulation;

import org.junit.jupiter.api.*;
import simulation.fields.Field;
import simulation.fields.Fish;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationSpaceTest {

    Field field;
    SimulationSpace simulationSpace;
    Snapshot snapshot;

    @BeforeEach
    public void beforeEach() {
        field = new Fish(new FishGenome(), new Vector(0,0 ));
        simulationSpace = new SimulationSpace(750, 750);

        simulationSpace.addField(field);

        simulationSpace.queueAddField(new Fish(new FishGenome(), new Vector(0, 0)));
        simulationSpace.queueAddField(new Fish(new FishGenome(), new Vector(0, 1)));
        simulationSpace.queueAddField(new Fish(new FishGenome(), new Vector(0, 2)));

        simulationSpace.queueRemoveField(field);

        snapshot = new Snapshot(new Simulation(750, 750));
    }

    @Test
    public void getTile() {
        assertEquals(field, simulationSpace.getTile(0, 0).getFields().get(0));
    }

    @Test
    public void getTileVector() {
        assertEquals(field, simulationSpace.getTile(new Vector(0, 0)).getFields().get(0));
    }

    @Test
    public void addField() {
        assertTrue(simulationSpace.getActiveFields().contains(field));
    }

    @Test
    public void removeField() {
        simulationSpace.removeField(field);
        assertFalse(simulationSpace.getActiveFields().contains(field));
    }

    @Test
    public void moveFieldOldPosNullified() {
        Vector newPos = new Vector(2, 2);
        Vector oldPos = field.getPosition();
        simulationSpace.moveField(newPos, field);
        assertEquals(0, simulationSpace.getTile(oldPos.x, oldPos.y).getFields().size());
    }

    @Test
    public void moveField() {
        Vector pos = new Vector(2, 2);
        simulationSpace.addField(field);
        simulationSpace.moveField(pos, field);
        assertEquals(field, simulationSpace.getTile(pos.x, pos.y).getFields().get(0));
    }

    @Test
    public void queueAddField() {
        simulationSpace.queueAddField(field);
        assertTrue(simulationSpace.getFieldsAddQueue().contains(field));
    }

    @Test
    public void queueRemoveField() {
        simulationSpace.queueRemoveField(field);
        assertTrue(simulationSpace.getFieldsRemoveQueue().contains(field));
    }

    @Test
    public void processQueueEmptyQueues() {
        simulationSpace.processQueue();
        assertTrue(simulationSpace.getFieldsAddQueue().size() == 0 && simulationSpace.getFieldsRemoveQueue().size() == 0);
    }

    @Test
    public void processQueueFieldAmount() {
        simulationSpace.processQueue();
        assertEquals(3, simulationSpace.getActiveFields().size());
    }

    @Test
    public void isNotWithinBounds() {
        assertFalse(simulationSpace.isWithinBounds(new Vector(50, 800)));
    }

    @Test
    public void isWithinBounds() {
        assertFalse(simulationSpace.isWithinBounds(new Vector(750, 750)));
    }

    @Test
    public void getWidth() {
        assertEquals(750, simulationSpace.getWidth());
    }

    @Test
    public void getHeight() {
        assertEquals(750, simulationSpace.getHeight());
    }

    @Test
    public void getNumActiveFields() {
        assertEquals(1, simulationSpace.getNumActiveFields());
    }

    @Test
    public void iteratorFunctioning() {
        assertTrue(simulationSpace.iterator().hasNext());
    }

    @Test
    public void getTiles() {
        assertTrue(750 == simulationSpace.getTiles().length);
    }

    @Test
    public void applySnapshot() {
        simulationSpace.applySnapshot(snapshot);
        assertEquals(0, snapshot.getCurrentTimeStep());
    }

}
