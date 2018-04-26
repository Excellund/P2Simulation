package simulation.fields;
import org.junit.jupiter.api.*;
import simulation.FishGenome;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FishTest {

    Fish fish;
    Fish fish2;

    @BeforeEach
    public void beforeEach() {
        fish = new Fish(new FishGenome(), new Vector(0,0));
        fish2 = new Fish(new FishGenome(), new Vector(0,1));
    }

    @Test
    public void getCompatibility() {

    }
}
