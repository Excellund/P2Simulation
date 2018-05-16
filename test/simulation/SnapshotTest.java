package simulation;

import com.sun.tools.javac.Main;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import simulation.fields.Carcass;
import simulation.fields.Fish;
import simulation.fields.FishEgg;
import utils.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class SnapshotTest {

    Simulation simulation;
    Snapshot snapshot;
    String path;

    @BeforeEach
    public void beforeEach() {
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
            Snapshot snapshot1 = snapshot;
            Snapshot snapshot2 = Snapshot.loadSnapshot(path);
            assertEquals(snapshot1, snapshot2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Snapshot failed to load");
        }
    }
}
