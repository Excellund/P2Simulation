package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VesselTest {

    Vessel vessel;

    @BeforeEach
    public void beforeEach() {
        vessel = new Vessel(2000, new Vector(500, 500), new Vector(1000, 1000), 0.5f);
    }

    @Test
    public void quotaIsSpent() {
        assertFalse(vessel.quotaIsSpent());
    }
}
