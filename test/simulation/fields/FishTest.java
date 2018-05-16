package simulation.fields;

import org.junit.jupiter.api.*;
import simulation.FishGenome;
import simulation.Settings;
import simulation.SimulationSpace;
import utils.Color;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FishTest {

    Fish fish;
    Fish fish2;
    SimulationSpace space;

    @BeforeEach
    public void beforeEach() {
        Settings.defaultAbbreviated();
        Settings.useAbbreviated();
        Settings.toFile("default");
        Settings.fromFile("default");
        space = new SimulationSpace(750, 750);
        fish = new Fish(new FishGenome(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, new Color(0, 0, 0), new FishGenome(), new FishGenome()), new Vector(0, 0));
        fish2 = new Fish(new FishGenome(0.4f, 0.3f, 0.5f, 0.52f, 0.51f, 0.2f, 0.3f, 0.34f, 0.21f, 0.15f, new Color(0, 0, 0), new FishGenome(), new FishGenome()), new Vector(0, 1));
    }

    @Test
    public void getCompatibility() {
        assertEquals(0.9464965f, fish.getCompatibility(fish2));
    }

    @Test
    public void updateDeadFish() {
        space.addField(fish);
        fish.subtractHealth(10000);
        fish.update(space);
        assertTrue(space.getFieldsRemoveQueue().contains(fish));
    }

    @Test
    public void isAlive() {
        assertTrue(fish.isAlive());
    }

    @Test
    public void getEnergy() {
        assertEquals(3, fish.getEnergy());
    }


}
