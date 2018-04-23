package simulation;
import org.junit.jupiter.api.*;
import simulation.Subjects.Fish;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TileTest {

    Tile tile;
    Fish fish;

    @BeforeEach
    public void beforeEach() {
        tile = new Tile(0);
        fish = new Fish(new FishGenome(), new Vector(0,0));
    }

    @Test
    public void addSubject() {
        tile.addSubject(fish);
        assertTrue(tile.getSubjects().contains(fish));
    }

    @Test
    public void removeSubject() {
        tile.addSubject(fish);
        tile.removeSubject(fish);
        assertFalse(tile.getSubjects().contains(fish));
    }

    @Test
    public void addDensity() {
        tile.addDensity(1000);
        assertEquals(1000, tile.getMuDensity());
    }

    @Test
    public void addDensityMax() {
        tile.addDensity(2500000);
        assertEquals(1000000, tile.getMuDensity());
    }

    @Test
    public void subtractDensity() {
        tile.addDensity(10000);
        tile.subtractDensity(5000);
        assertEquals(5000, tile.getMuDensity());
    }

    @Test
    public void subtractDensityMinimum() {
        tile.addDensity(10000);
        tile.subtractDensity(50000);
        assertEquals(0, tile.getMuDensity());
    }
}
