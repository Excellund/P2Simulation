package simulation;

import simulation.fields.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tile {
    private List<Field> fields;
    private int muDensity;

    public Tile(int muDensity) {
        //creates a new tile with a specified mu density
        this.muDensity = muDensity;
        fields = Collections.synchronizedList(new ArrayList<>());
    }

    public void addField(Field field) {
        //adds a field to the tile
        fields.add(field);
    }

    public void removeField(Field field) {
        //removes a field from the tile
        fields.remove(field);
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

    public List<Field> getFields() {
        return fields;
    }

    public int getMuDensity() {
        return muDensity;
    }
}
