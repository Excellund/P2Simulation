package simulation;

import java.util.ArrayList;

public class Tile {
    private ArrayList<Field> subjects;
    private int muDensity;

    public Tile(int muDensity) {
        this.muDensity = muDensity;
        subjects = new ArrayList<>();
    }

    public void addSubject(Field subject) {
        subjects.add(subject);
    }

    public void removeSubject(Field subject) {
        subjects.remove(subject);
    }

    public void subtractDensity(int amount) {
        muDensity -= amount;

        if (muDensity < 0) {
            muDensity = 0;
        }
    }

    public void addDensity(int amount) {
        muDensity += amount;

        if (muDensity > 1000000) {
            muDensity = 1000000;
        }
    }

    // Getters

    public ArrayList<Field> getSubjects() {
        return subjects;
    }

    public int getMuDensity() {
        return muDensity;
    }
}
