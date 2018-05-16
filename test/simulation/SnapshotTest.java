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
        assertTrue(Snapshot.saveSnapshot(path, snapshot));
    }

    @Test
    public void saveSnapshotException() {
        assertFalse(Snapshot.saveSnapshot("snapshots", snapshot));
    }

    @Test
    public void loadSnapshot() {
        Snapshot other = Snapshot.loadSnapshot(path);
        assertTrue(snapshot.equals(other));
    }
}
