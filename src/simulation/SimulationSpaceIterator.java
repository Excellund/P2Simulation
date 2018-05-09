package simulation;

import simulation.fields.Field;

import java.util.List;
import java.util.ListIterator;

public class SimulationSpaceIterator implements ListIterator<Field> {

    ListIterator<Field> iterator;
    SimulationSpace space;
    Field currentElement = null;

    SimulationSpaceIterator(SimulationSpace space, List<Field> activeSubjects) {
        this.space = space;
        iterator = activeSubjects.listIterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Field next() {
        currentElement = iterator.next();
        return currentElement;
    }

    @Override
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    @Override
    public Field previous() {
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
        //Remove from list iterator
        iterator.remove();

        //Remove from simulation
        if (currentElement != null) {
            space.getTile(currentElement.getPosition()).removeSubject(currentElement);
        }
    }

    @Override
    public void set(Field f) {
        iterator.set(f);
    }

    @Override
    public void add(Field f) {
        iterator.add(f);
    }
}
