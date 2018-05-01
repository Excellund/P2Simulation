package simulation;

import simulation.fields.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tile {
    private List<Field> subjects;
    private int muDensity;

    public Tile(int muDensity) {
        //creates a new tile with a specified mu density
        this.muDensity = muDensity;
        subjects = Collections.synchronizedList(new ArrayList<>());
    }

    public void addSubject(Field subject) {
        //adds a field to the tile
        subjects.add(subject);
    }

    public void removeSubject(Field subject) {
        //removes a field from the tile
        subjects.remove(subject);
    }

    public void addDensity(int amount) {
        //adds a specified amount to the mu density of plankton,
        //limiting the effect if the increment would lead to an illegal density level
        muDensity += amount;

        if (muDensity > 1000000) {
            muDensity = 1000000;
        }
    }

    public void subtractDensity(int amount) {
        //removes a specified amount of plankton from the tile,
        //setting the density to 0 should it go below 0.
        muDensity -= amount;

        if (muDensity < 0) {
            muDensity = 0;
        }
    }

    // Getters

    public List<Field> getSubjects() {
        return subjects;
    }

    public int getMuDensity() {
        return muDensity;
    }
}
