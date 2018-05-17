package utils;

import java.util.Random;

public class CountingRandom extends Random {

    private static final CountingRandom globalRandom = new CountingRandom();

    private long initialSeed = 0;
    private long nextCounter = 0;

    private CountingRandom() {
        super();
        initialSeed = nextLong();
        setState(initialSeed, 0);
    }

    //next is called by all of Randoms random functions to generate random values. The number of times it is called is therefore counted, in order to set the state later.
    @Override
    protected int next(int bits) {
        nextCounter++;
        return super.next(bits);
    }

    //Sets the random state of the simulation
    public void setState(long seed, long counter) {
        initialSeed = seed;
        nextCounter = counter;

        setSeed(seed); //Use setSeed function inherited from Random class

        for (long i = 0; i < counter; i++) {
            next(0);
        }
    }

    public long getCounter() {
        return nextCounter;
    }

    //Gets the seed the simulation was started with
    public long getInitialSeed() {
        return initialSeed;
    }

    //Gets an instance of CountingRandom accessible anywhere.
    public static CountingRandom getInstance() {
        return globalRandom;
    }
}
