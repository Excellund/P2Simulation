package simulation;
import utils.Vector;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VesselTest {

    Vessel vessel;

    @BeforeEach
    public void beforeEach() {
        vessel = new Vessel(2000, new Vector(500, 500), new Vector(1000, 1000),0.5f);
    }

    @Test
    public void quotaIsSpent() {
        assertFalse(vessel.quotaIsSpent());
    }
}
