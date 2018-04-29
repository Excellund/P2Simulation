package simulation;

import simulation.fields.Field;
import utils.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class SimulationSpace implements Iterable<Field> {
    private Tile[][] tiles;
    private ArrayList<Field> activeSubjects;
    private ArrayList<Field> subjectsAddQueue;
    private ArrayList<Field> subjectsRemoveQueue;
    private int width, height;

    public SimulationSpace(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[height][width];
        activeSubjects = new ArrayList<>();
        subjectsAddQueue = new ArrayList<>();
        subjectsRemoveQueue = new ArrayList<>();

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
        tiles[fieldPos.y][fieldPos.x].addSubject(field);
        activeSubjects.add(field);
    }

    //Removes a field in the simulation
    public void removeField(Field field) {
        utils.Vector pos = field.getPosition();
        tiles[pos.y][pos.x].removeSubject(field);
        activeSubjects.remove(field);
    }

    //Moves a field within the simulation to a specific tile position
    public void moveField(utils.Vector tilePos, Field field) {
        utils.Vector oldPos = field.getPosition();
        field.setPosition(tilePos);
        tiles[oldPos.y][oldPos.x].removeSubject(field);
        tiles[tilePos.y][tilePos.x].addSubject(field);
    }

    //Queues a field to be added later, in order to avoid conflicts while iterating over all fields
    public void queueAddField(Field field) {
        subjectsAddQueue.add(field);
    }

    //Queues a field to be removed later, in order to avoid conflicts while iterating over all fields
    public void queueRemoveField(Field field) {
        subjectsRemoveQueue.add(field);
    }

    //Processes fields from methods queueAddField and queueRemoveField
    public void processQueue() {
        //Process fields to be added
        for (Field field : subjectsAddQueue) {
            addField(field);
        }

        //Process fields to be removed
        for (Field field : subjectsRemoveQueue) {
            removeField(field);
        }

        subjectsAddQueue.clear();
        subjectsRemoveQueue.clear();
    }

    //Returns a list iterator to iterate all active fields in the simulation
    public ListIterator<Field> listIterator() {
        return new SimulationSpaceIterator(this, activeSubjects);
    }

    //Returns a iterator to iterate all active fields in the simulation
    @Override
    public Iterator<Field> iterator() {
        return new SimulationSpaceIterator(this, activeSubjects);
    }

    //Checks whether a position is within the bound of the simulation
    public boolean isWithinBounds(Vector tilePos) {
        return tilePos.x >= 0 && tilePos.x < width && tilePos.y >= 0 && tilePos.y < height;
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
        return activeSubjects.size();
    }
}
