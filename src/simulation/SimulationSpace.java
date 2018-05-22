package simulation;

import simulation.fields.Field;
import utils.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SimulationSpace implements Iterable<Field> {
    private Tile[][] tiles;
    private List<Field> activeFields;
    private List<Field> fieldsAddQueue;
    private List<Field> fieldsRemoveQueue;
    private int width, height;

    public SimulationSpace(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[height][width];
        activeFields = new ArrayList<>();
        fieldsAddQueue = new ArrayList<>();
        fieldsRemoveQueue = new ArrayList<>();

        //Initialize tiles
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                tiles[y][x] = new Tile(0);
            }
        }
    }

    //Adds a field to the simulation.
    public void addField(Field field) {
        Vector fieldPos = field.getPosition();
        tiles[fieldPos.y][fieldPos.x].addField(field);
        activeFields.add(field);
    }

    //Removes a field in the simulation
    public void removeField(Field field) {
        utils.Vector pos = field.getPosition();
        tiles[pos.y][pos.x].removeField(field);
        activeFields.remove(field);
    }

    //Moves a field within the simulation to a specific tile position
    public void moveField(Vector tilePos, Field field) {
        Vector oldPos = field.getPosition();
        field.setPosition(tilePos);
        tiles[oldPos.y][oldPos.x].removeField(field);
        tiles[tilePos.y][tilePos.x].addField(field);
    }

    //Queues a field to be added later, in order to avoid conflicts while iterating over all fields
    public void queueAddField(Field field) {
        fieldsAddQueue.add(field);
    }

    //Queues a field to be removed later, in order to avoid conflicts while iterating over all fields
    public void queueRemoveField(Field field) {
        fieldsRemoveQueue.add(field);
    }

    //Processes fields from methods queueAddField and queueRemoveField
    public void processQueue() {
        //Process fields to be added
        for (Field field : fieldsAddQueue) {
            addField(field);
        }

        //Process fields to be removed
        for (Field field : fieldsRemoveQueue) {
            removeField(field);
        }

        fieldsAddQueue.clear();
        fieldsRemoveQueue.clear();
    }

    //Returns an iterator to iterate all active fields in the simulation
    @Override
    public Iterator<Field> iterator() {
        return new SimulationSpaceIterator(this, activeFields);
    }

    //Checks whether a position is within the bound of the simulation
    public boolean isWithinBounds(Vector tilePos) {
        return tilePos.x >= 0 && tilePos.x < width && tilePos.y >= 0 && tilePos.y < height;
    }

    public void applySnapshot(Snapshot snapshot) {
        width = snapshot.getWidth();
        height = snapshot.getHeight();

        tiles = new Tile[height][width];
        activeFields = new ArrayList<>();
        fieldsAddQueue = new ArrayList<>();
        fieldsRemoveQueue = new ArrayList<>();

        int planktonDensities[][] = snapshot.getPlanktonDensities();

        //Initialize tiles
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                tiles[y][x] = new Tile(planktonDensities[y][x]);
            }
        }

        activeFields.addAll(Arrays.asList(snapshot.getFields()));

        //Add fields to tiles
        for (Field field : activeFields) {
            Vector fieldPos = field.getPosition();
            tiles[fieldPos.y][fieldPos.x].addField(field);
        }
    }

    //Getters
    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public Tile getTile(Vector pos) {
        return getTile(pos.x, pos.y);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumActiveFields() {
        return activeFields.size();
    }

    public List<Field> getActiveFields() {
        return activeFields;
    }

    public List<Field> getFieldsAddQueue() {
        return fieldsAddQueue;
    }

    public List<Field> getFieldsRemoveQueue() {
        return fieldsRemoveQueue;
    }
}
