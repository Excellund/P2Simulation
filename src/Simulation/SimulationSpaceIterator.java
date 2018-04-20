package Simulation;

import java.util.ArrayList;
import java.util.ListIterator;

public class SimulationSpaceIterator implements ListIterator {

    ListIterator<Field> iterator;
    SimulationSpace space;
    Field current = null;

    SimulationSpaceIterator(SimulationSpace space, ArrayList<Field> activeSubjects) {
        this.space = space;
        iterator = activeSubjects.listIterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {
        current = iterator.next();
        return current;
    }

    @Override
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    @Override
    public Object previous() {
        return iterator.previous();
    }

    @Override
    public int nextIndex() {
        return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        return iterator.previousIndex();
    }

    @Override
    public void remove() {
        iterator.remove();
        if (current != null) {
            space.getTile(current.getPosition()).removeSubject(current);
        }
    }

    @Override
    public void set(Object o) {
        iterator.set((Field) o);
    }

    @Override
    public void add(Object o) {
        iterator.add((Field) o);
    }
}
