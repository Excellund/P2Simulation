package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VesselTest {

    SimulationSpace space;
    Vessel vessel;

    @BeforeEach
    public void beforeEach() {
        space = new SimulationSpace(750, 750);
        vessel = new Vessel(2000, new Vector(1, 1), new Vector(2, 2), 0.5f);
    }

    @Test
    public void getQuota() {
        assertEquals(2000, vessel.getQuota());
    }

    @Disabled
    public void getNet() {
        assertEquals(new Net(0.5f), vessel.getNet());
    }

    @Test
    public void timeStep() {
        double oldX = vessel.getTemporaryX(), oldY = vessel.getTemporaryY();
        vessel.timeStep(space);
        assertTrue(vessel.getTemporaryX());
    }

    @Test
    public void quotaIsSpent() {
        assertFalse(vessel.quotaIsSpent());
    }
}
