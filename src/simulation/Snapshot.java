package simulation;

import simulation.fields.Carcass;
import simulation.fields.Field;
import simulation.fields.Fish;
import simulation.fields.FishEgg;
import utils.CountingRandom;
import utils.Vector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Snapshot {
    private static final byte GROUP_SEPARATOR = 0x1D;

    private int width, height;
    private int numFish, numFishEgg, numCarcass, numVessels;
    private long randomSeed;
    private long randomCounter;

    private Fish[] fish;
    private FishEgg[] fishEggs;
    private Carcass[] carcasses;
    private Vessel[] vessels;

    private int[][] planktonDensities;

    private Snapshot() {

    }

    public Snapshot(Simulation sim) {
        width = sim.getSpace().getWidth();
        height = sim.getSpace().getHeight();
        numFish = 0;
        numFishEgg = 0;
        numCarcass = 0;
        numVessels = sim.getVessels().size();
        randomSeed = CountingRandom.getInstance().getInitialSeed();
        randomCounter = CountingRandom.getInstance().getCounter();

        //Get number of Fish, FishEgg, Carcass
        for (Field field : sim.getSpace()) {
            if (field instanceof Fish) {
                numFish++;
            } else if (field instanceof FishEgg) {
                numFishEgg++;
            } else if (field instanceof Carcass) {
                numCarcass++;
            }
        }

        fish = new Fish[numFish];
        fishEggs = new FishEgg[numFishEgg];
        carcasses = new Carcass[numCarcass];
        vessels = new Vessel[sim.getVessels().size()];

        int countFish = 0;
        int countFishEgg = 0;
        int countCarcass = 0;
        for (Field field : sim.getSpace()) {
            if (field instanceof Fish) {
                fish[countFish] = (Fish) field;
                countFish++;
            } else if (field instanceof FishEgg) {
                fishEggs[countFishEgg] = (FishEgg) field;
                countFishEgg++;
            } else if (field instanceof Carcass) {
                carcasses[countCarcass] = (Carcass) field;
                countCarcass++;
            }
        }

        sim.getVessels().toArray(vessels);

        planktonDensities = new int[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                planktonDensities[y][x] = sim.getSpace().getTile(x, y).getMuDensity();
                //writeInt(stream, snapshot.sim.getSpace().getTile(x, y).getMuDensity());
            }
        }
    }

    public static boolean saveSnapshot(String path, Snapshot snapshot) {
        try (OutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)))) {
            //Write header containing metadata
            writeInt(stream, snapshot.width);
            writeInt(stream, snapshot.height);
            writeInt(stream, snapshot.numFish);
            writeInt(stream, snapshot.numFishEgg);
            writeInt(stream, snapshot.numCarcass);
            writeInt(stream, snapshot.numVessels);
            writeLong(stream, snapshot.randomSeed);
            writeLong(stream, snapshot.randomCounter);
            stream.write(GROUP_SEPARATOR);

            //Write plankton densities
            for (int y = 0; y < snapshot.height; ++y) {
                for (int x = 0; x < snapshot.width; ++x) {
                    writeInt(stream, snapshot.planktonDensities[y][x]);
                }
            }
            stream.write(GROUP_SEPARATOR);

            //Write fish
            for (Fish fish : snapshot.fish) {
                writeFish(stream, fish);
            }
            stream.write(GROUP_SEPARATOR);

            //Write fishEggs
            for (FishEgg egg : snapshot.fishEggs) {
                writeFishEgg(stream, egg);
            }
            stream.write(GROUP_SEPARATOR);

            //Write carcasses
            for (Carcass carcass : snapshot.carcasses) {
                writeCarcass(stream, carcass);
            }
            stream.write(GROUP_SEPARATOR);

            //Write vessels
            for (Vessel vessel : snapshot.vessels) {
                writeVessel(stream, vessel);
            }
            stream.write(GROUP_SEPARATOR);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static Snapshot loadSnapshot(String path) {
        Snapshot snapshot = new Snapshot();

        try (InputStream stream = new BufferedInputStream(Files.newInputStream(Paths.get(path)))) {
            //Read header containing metadata
            snapshot.width = readInt(stream);
            snapshot.height = readInt(stream);
            snapshot.numFish = readInt(stream);
            snapshot.numFishEgg = readInt(stream);
            snapshot.numCarcass = readInt(stream);
            snapshot.numVessels = readInt(stream);
            snapshot.randomSeed = readLong(stream);
            snapshot.randomCounter = readLong(stream);

            if (stream.read() != GROUP_SEPARATOR) {
                return null;
            }

            //Read plankton densities
            snapshot.planktonDensities = new int[snapshot.height][snapshot.width];
            for (int y = 0; y < snapshot.height; y++) {
                for (int x = 0; x < snapshot.width; x++) {
                    snapshot.planktonDensities[y][x] = readInt(stream);
                }
            }

            if (stream.read() != GROUP_SEPARATOR) { return null; }

            //Read fish
            snapshot.fish = new Fish[snapshot.numFish];
            for (int i = 0; i < snapshot.numFish; i++) {
                snapshot.fish[i] = readFish(stream);
            }

            if (stream.read() != GROUP_SEPARATOR) { return null; }

            //Read fish eggs
            snapshot.fishEggs = new FishEgg[snapshot.numFishEgg];
            for (int i = 0; i < snapshot.numFishEgg; i++) {
                snapshot.fishEggs[i] = readFishEgg(stream);
            }

            if (stream.read() != GROUP_SEPARATOR) { return null; }

            //Read carcasses
            snapshot.carcasses = new Carcass[snapshot.numCarcass];
            for (int i = 0; i < snapshot.numCarcass; i++) {
                snapshot.carcasses[i] = readCarcass(stream);
            }

            if (stream.read() != GROUP_SEPARATOR) { return null; }

            //Read vessels
            snapshot.vessels = new Vessel[snapshot.numVessels];
            for (int i = 0; i < snapshot.numVessels; i++) {
                snapshot.vessels[i] = readVessel(stream);
            }
        } catch (IOException e) {
            return null;
        }

        return snapshot;
    }

    private static void writeInt(OutputStream stream, int value) throws IOException {
        stream.write(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    private static void writeLong(OutputStream stream, long value) throws IOException {
        stream.write(ByteBuffer.allocate(Long.BYTES).putLong(value).array());
    }

    private static void writeFloat(OutputStream stream, float value) throws IOException {
        stream.write(ByteBuffer.allocate(Float.BYTES).putFloat(value).array());
    }

    private static void writeDouble(OutputStream stream, double value) throws IOException {
        stream.write(ByteBuffer.allocate(Double.BYTES).putDouble(value).array());
    }

    private static void writeVector(OutputStream stream, Vector vec) throws IOException {
        writeInt(stream, vec.x);
        writeInt(stream, vec.y);
    }

    private static void writeFishGenome(OutputStream stream, FishGenome genome) throws IOException {
        for (float value : genome.getParentGenomeA().getArray()) {
            writeFloat(stream, value);
        }

        for (float value : genome.getParentGenomeB().getArray()) {
            writeFloat(stream, value);
        }

        for (float value : genome.getArray()) {
            writeFloat(stream, value);
        }
    }

    private static void writeFish(OutputStream stream, Fish fish) throws IOException {
        writeVector(stream, fish.getPosition());
        writeFloat(stream, fish.getHealth());
        writeFloat(stream, fish.getEnergy());
        writeFloat(stream, fish.getSize());
        writeFloat(stream, fish.getSpeed());
        writeFishGenome(stream, fish.getGenome());
    }

    private static void writeFishEgg(OutputStream stream, FishEgg egg) throws IOException {
        writeVector(stream, egg.getPosition());
        writeInt(stream, egg.getNumEggs());
        writeInt(stream, egg.getTimeBeforeHatch());
        writeFishGenome(stream, egg.getGenome());
    }

    private static void writeCarcass(OutputStream stream, Carcass carcass) throws IOException {
        writeVector(stream, carcass.getPosition());
        writeInt(stream, carcass.getNutrition());
    }

    private static void writeVessel(OutputStream stream, Vessel vessel) throws IOException {
        writeInt(stream, vessel.getQuota());
        writeVector(stream, vessel.getBow());
        writeVector(stream, vessel.getSought());
        writeVector(stream, vessel.getMax());
        writeDouble(stream, vessel.getTemporaryX());
        writeDouble(stream, vessel.getTemporaryY());
        writeFloat(stream, vessel.getNet().getFavoredMorphology());
        writeInt(stream, vessel.getNet().getFish().size());

        for (Fish fish : vessel.getNet().getFish()) {
            writeFish(stream, fish);
        }
    }

    private static int readInt(InputStream stream) throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        stream.read(bytes);
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES).put(bytes);
        return buf.getInt(0);
    }

    private static long readLong(InputStream stream) throws IOException {
        byte[] bytes = new byte[Long.BYTES];
        stream.read(bytes);
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES).put(bytes);
        return buf.getLong(0);
    }

    private static float readFloat(InputStream stream) throws IOException {
        byte[] bytes = new byte[Float.BYTES];
        stream.read(bytes);
        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES).put(bytes);
        return buf.getFloat(0);
    }

    private static double readDouble(InputStream stream) throws IOException {
        byte[] bytes = new byte[Double.BYTES];
        stream.read(bytes);
        ByteBuffer buf = ByteBuffer.allocate(Double.BYTES).put(bytes);
        return buf.getDouble(0);
    }

    private static Vector readVector(InputStream stream) throws IOException {
        return new Vector(readInt(stream), readInt(stream));
    }

    private static FishGenome readFishGenome(InputStream stream) throws IOException {
        float[] attributesParentA = new float[FishGenome.NUM_ATTRIBUTES];
        float[] attributesParentB = new float[FishGenome.NUM_ATTRIBUTES];
        float[] attributes = new float[FishGenome.NUM_ATTRIBUTES];

        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributesParentA[i] = readFloat(stream);
        }

        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributesParentB[i] = readFloat(stream);
        }

        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributes[i] = readFloat(stream);
        }

        FishGenome parentA = new FishGenome(attributesParentA, null, null);
        FishGenome parentB = new FishGenome(attributesParentB, null, null);
        return new FishGenome(attributes, parentA, parentB);
    }

    private static Fish readFish(InputStream stream) throws IOException {
        Vector position = readVector(stream);
        float health = readFloat(stream);
        float energy = readFloat(stream);
        float size = readFloat(stream);
        float speed = readFloat(stream);
        FishGenome genome = readFishGenome(stream);

        return new Fish(position, health, energy, size, speed, genome);
    }

    private static FishEgg readFishEgg(InputStream stream) throws IOException {
        Vector position = readVector(stream);
        int numEggs = readInt(stream);
        int timeBeforeHatch = readInt(stream);
        FishGenome genome = readFishGenome(stream);

        return new FishEgg(position, numEggs, timeBeforeHatch, genome);
    }

    private static Carcass readCarcass(InputStream stream) throws IOException {
        Vector position = readVector(stream);
        int nutrition = readInt(stream);

        return new Carcass(nutrition, position);
    }

    private static Vessel readVessel(InputStream stream) throws IOException {
        int quota = readInt(stream);
        Vector bow = readVector(stream);
        Vector sought = readVector(stream);
        Vector max = readVector(stream);
        double tempX = readDouble(stream);
        double tempY = readDouble(stream);
        float netFavoredMorphology = readFloat(stream);
        int netNumFish = readInt(stream);
        Fish[] netFish = new Fish[netNumFish];

        for (int i = 0; i < netNumFish; i++) {
            netFish[i] = readFish(stream);
        }

        return new Vessel(quota, bow, sought, max, tempX, tempY, netFavoredMorphology, netFish);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public long getRandomCounter() {
        return randomCounter;
    }

    public Fish[] getFish() {
        return fish;
    }

    public FishEgg[] getFishEggs() {
        return fishEggs;
    }

    public Carcass[] getCarcasses() {
        return carcasses;
    }

    public Vessel[] getVessels() {
        return vessels;
    }

    public int[][] getPlanktonDensities() {
        return planktonDensities;
    }
}