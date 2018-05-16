package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulation.fields.Fish;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NetTest {

    Net net;
    Net netCopy;

    @BeforeEach
    public void beforeEach() {
        net = new Net(0.5f, new Fish[]{new Fish(new FishGenome(), new Vector(0, 0))});
        netCopy = new Net(0.5f, new Fish[]{new Fish(new FishGenome(), new Vector(0, 0))});
    }

    @Test
    public void resetTrawl() {
        assertEquals(1, net.resetTrawl());
    }

    @Test
    public void resetTrawlEmptyNet() {
        net.resetTrawl();
        assertEquals(0, net.getFish().size());
    }

    @Test
    public void getFavoredMorphology() {
        assertEquals(0.5f, net.getFavoredMorphology());
    }
}
