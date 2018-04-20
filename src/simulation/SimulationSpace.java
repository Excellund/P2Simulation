package simulation;

import utils.Vector;

import java.util.*;

public class SimulationSpace implements Iterable {
    private Tile[][] tiles;
    private ArrayList<Field> activeSubjects;
    private int width, height;

    public SimulationSpace(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[height][width];
        activeSubjects = new ArrayList<>();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                tiles[y][x] = new Tile(0);
            }
        }
    }

    public void addField(utils.Vector tilePos, Field field) {
        field.setPosition(tilePos);
        tiles[tilePos.y][tilePos.x].addSubject(field);
        activeSubjects.add(field);
    }

    public void removeField(utils.Vector tilePos, Field field) {
        utils.Vector pos = field.getPosition();
        tiles[pos.y][pos.x].removeSubject(field);
        activeSubjects.remove(field);
    }

    public void moveField(utils.Vector tilePos, Field field) {
        utils.Vector oldPos = field.getPosition();
        field.setPosition(tilePos);
        tiles[oldPos.y][oldPos.x].removeSubject(field);
        tiles[tilePos.y][tilePos.x].addSubject(field);
    }

    public Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public Tile getTile(Vector pos) {
        return getTile(pos.x, pos.y);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public ListIterator<Field> listIterator() {
        return new SimulationSpaceIterator(this, activeSubjects);
    }

    @Override
    public Iterator iterator() {
        return new SimulationSpaceIterator(this, activeSubjects);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long tileDensity(int xLower, int xHigher, int yLower, int yHigher) {
        long combined = 0;

        for (int y = yLower; y < yHigher; ++y) {
            for (int x = xLower; x < xHigher; ++x) {
                combined += tiles[y][x].getMuDensity();
            }
        }

        return combined;
    }

    public int getNumActiveFields() {
        return activeSubjects.size();
    }
}
