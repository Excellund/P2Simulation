package simulation;

import utils.CountingRandom;
import utils.Vector;
import utils.VectorTransformer;

import java.util.ArrayList;
import java.util.LinkedList;

public class Net {
    private LinkedList<Fish> fish;
    private float favoredMorphology;
    private final double[][] entry = {{-90, -90}, {-10, 10}};

    public Net(float favoredMorphology) {
        this.favoredMorphology = favoredMorphology;

        fish = new LinkedList<>();
    }

    private boolean isOutOfBounds(Vector requested, Vector max) {
        return requested.x < 0 || requested.x > max.x || requested.y < 0 || requested.y > max.y;
    }

    public void timeStep(int direction, double scale, Vector bow, Vector[] stern, SimulationSpace space) {
        if (isOutOfBounds(bow, new Vector(space.getWidth(), space.getHeight())) ||
                isOutOfBounds(stern[0], new Vector(space.getWidth(), space.getHeight())) ||
                isOutOfBounds(stern[0], new Vector(space.getWidth(), space.getHeight()))) {
            return; //don't perform fishing related actions when out of bounds
        }

        double[][] entryLine = VectorTransformer.rotatePolygon(entry, new Vector(0, 0), direction);

        entryLine = VectorTransformer.scalePolygon(entryLine, scale);
        entryLine = VectorTransformer.translatePolygon(entryLine, bow);

        double x = entryLine[0][1] - entryLine[0][0];
        double y = entryLine[1][1] - entryLine[1][0];
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double vecX = x / length;
        double vecY = y / length;

        for (int multiplier = 1; multiplier < length; ++multiplier) {
            moveFish(space, new Vector((int) (vecX * multiplier + entryLine[0][0]), (int) (vecY * multiplier + entryLine[1][0])));
            moveFish(space, new Vector((int) (vecX * multiplier + entryLine[0][0] + 1), (int) (vecY * multiplier + entryLine[1][0])));
            moveFish(space, new Vector((int) (vecX * multiplier + entryLine[0][0]), (int) (vecY * multiplier + entryLine[1][0]) + 1));
        }
    }

    private void moveFish(SimulationSpace space, Vector tile) {
        if (tile.x > space.getWidth() - 1 || tile.x < 0 || tile.y > space.getHeight() - 1 || tile.y < 0) {
            return;
        }

        ArrayList<Field> tileEntities = space.getTile(tile.x, tile.y).getSubjects();

        for (Field subject : tileEntities) {
            if (subject instanceof Fish && !fishEscapes((Fish) subject)) {
                fish.add((Fish) subject);
                space.queueRemoveField(subject);
            }
        }
    }

    private boolean fishEscapes(Fish subject) {
        float selectivity = (float) (1 / (1 + Math.pow(Math.E, -Settings.WIDTH_STEEPNESS * (subject.getSize() - favoredMorphology))));

        return CountingRandom.getInstance().nextFloat() < 1 - selectivity;
    }

    public int resetTrawl() {
        int length = fish.size();

        fish.clear();

        return length;
    }

    public float getFavoredMorphology() {
        return favoredMorphology;
    }

    public LinkedList<Fish> getFish() {
        return fish;
    }
}