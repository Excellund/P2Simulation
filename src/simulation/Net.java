package simulation;

import simulation.fields.Field;
import simulation.fields.Fish;
import utils.CountingRandom;
import utils.Vector;
import utils.VectorTransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Net {
    private List<Fish> fish;
    private float favoredMorphology;
    private final double[][] entry = {{-90, -90}, {-10, 10}};

    public Net(float favoredMorphology) {
        //creates a net with a specified favored selection in terms of length
        this.favoredMorphology = favoredMorphology;

        fish = Collections.synchronizedList(new LinkedList<>());
    }

    public Net(float favoredMorphology, Fish[] fish) {
        //creates a net with and initial population of fish.
        //This is used for snapshot functionality.
        this.favoredMorphology = favoredMorphology;
        this.fish = new LinkedList<>(Arrays.asList(fish));
    }

    private boolean isOutOfBounds(Vector requested, Vector max) {
        //returns a boolean value specifying whether the requested vector is out of bounds in terms of the simulation space
        return requested.x < 0 || requested.x > max.x || requested.y < 0 || requested.y > max.y;
    }

    public void timeStep(int direction, Vector bow, Vector[] stern, SimulationSpace space) {
        //should be called once every time-step.
        //Calculates collision between the net opening and fish,
        //determining whether said fish should be caught or escape
        //based on probability and morphology.
        if (isOutOfBounds(bow, new Vector(space.getWidth(), space.getHeight())) ||
                isOutOfBounds(stern[0], new Vector(space.getWidth(), space.getHeight())) ||
                isOutOfBounds(stern[0], new Vector(space.getWidth(), space.getHeight()))) {
            return; //don't perform fishing related actions when out of bounds
        }

        double[][] entryLine = VectorTransformer.rotatePolygon(entry, new Vector(0, 0), direction);

        entryLine = VectorTransformer.scalePolygon(entryLine, Settings.VESSEL_SCALE);
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
        //scans through the fields on a tile colliding with the net opening.
        //determines whether each fish should be caught or escape.
        if (tile.x > space.getWidth() - 1 || tile.x < 0 || tile.y > space.getHeight() - 1 || tile.y < 0) {
            return;
        }

        List<Field> tileEntities = space.getTile(tile.x, tile.y).getFields();

        for (Field subject : tileEntities) {
            if (subject instanceof Fish && !fishEscapes((Fish) subject)) {
                fish.add((Fish) subject);
                space.queueRemoveField(subject);
            }
        }
    }

    private boolean fishEscapes(Fish subject) {
        //returns a boolean determining whether the input fish escapes the net or gets caught.
        //This is based on probability following a logistic growth curve based on the relationship between the fish' morphology
        //and the net's selectivity.
        float selectivity = (float) (1 / (1 + Math.pow(Math.E, -Settings.WIDTH_STEEPNESS * (subject.getSize() - favoredMorphology))));

        return CountingRandom.getInstance().nextFloat() < 1 - selectivity;
    }

    public int resetTrawl() {
        //empties the net and returns an integer specifying the number of fish caught
        int length = fish.size();

        fish.clear();

        return length;
    }

    public float getFavoredMorphology() {
        return favoredMorphology;
    }

    public List<Fish> getFish() {
        return fish;
    }
}