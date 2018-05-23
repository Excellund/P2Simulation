package simulation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simulation.fields.Carcass;
import simulation.fields.Fish;
import simulation.fields.FishEgg;
import utils.Vector;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SnapshotTest {

    private static Simulation simulation;
    private static Snapshot snapshot;
    private static String path;

    @BeforeAll
    public static void beforeAll() {
        Settings.defaultAbbreviated();
        Settings.useAbbreviated();
        Settings.toFile("default");
        Settings.fromFile("default");

        simulation = new Simulation(750, 750);

        simulation.getSpace().queueAddField(new Fish(new FishGenome(), new Vector(0, 0)));
        simulation.getSpace().queueAddField(new FishEgg(new Vector(0, 1), new FishGenome(), 1000));
        simulation.getSpace().queueAddField(new Carcass(1000, new Vector(0, 2)));
        simulation.getSpace().processQueue();

        snapshot = new Snapshot(simulation);

        path = "snapshots/test";
    }

    @Test
    public void saveSnapshot() {
        try {
            Snapshot.saveSnapshot(path, snapshot);
        } catch (IOException e) {
            fail("Failed to save snapshot");
        }
    }

    @Test
    public void saveSnapshotException() {
        try {
            Snapshot.saveSnapshot("snapshots", snapshot);
            fail("Expected exception not thrown");
        } catch (IOException e) {
        }
    }

    @Test
    public void loadSnapshot() {
        try {
            saveSnapshot();

            Snapshot snapshot1 = snapshot;
            Snapshot snapshot2 = Snapshot.loadSnapshot(path);

            assertEquals(snapshot1, snapshot2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Snapshot failed to load");
        }
    }
}
