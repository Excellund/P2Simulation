package utils;

import java.util.Random;

public class CountingRandom extends Random {

    private static final CountingRandom globalRandom = new CountingRandom();

    private long nextCounter = 0;

    @Override
    protected int next(int bits) {
        nextCounter++;
        return super.next(bits);
    }

    public void setState(long seed, long counter) {
        setSeed(seed);

        for (int i = 0; i < counter; i++) {
            next(0);
        }
    }

    public long getCounter() {
        return nextCounter;
    }

    public static CountingRandom getInstance() {
        return globalRandom;
    }
}
