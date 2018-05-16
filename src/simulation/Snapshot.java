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
    private static final byte RECORD_SEPARATOR = 0x1E;

    private int width, height;
    private int numFish, numFishEgg, numCarcass, numVessels;
    private long randomSeed;
    private long randomCounter;
    private long currentTimeStep;

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
        currentTimeStep = sim.getCurrentTimeStep();

        //Get number of Fish, FishEggs, Carcasses
        for (Field field : sim.getSpace()) {
            if (field instanceof Fish) {
                numFish++;
            } else if (field instanceof FishEgg) {
                numFishEgg++;
            } else if (field instanceof Carcass) {
                numCarcass++;
            }
        }

        //Assign space to the Fish, FishEggs, Carcasses
        fish = new Fish[numFish];
        fishEggs = new FishEgg[numFishEgg];
        carcasses = new Carcass[numCarcass];
        vessels = new Vessel[sim.getVessels().size()];

        //Populate the arrays
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

        //Get plankton densities
        planktonDensities = new int[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                planktonDensities[y][x] = sim.getSpace().getTile(x, y).getMuDensity();
            }
        }
    }

    public static void saveSnapshot(String path, Snapshot snapshot) throws IOException {
        //Wrap stream in try-catch to handle automatic disposing of stream
        try(OutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)))) {
            //Write header containing metadata
            writeInt(stream, snapshot.width);
            writeInt(stream, snapshot.height);
            writeInt(stream, snapshot.numFish);
            writeInt(stream, snapshot.numFishEgg);
            writeInt(stream, snapshot.numCarcass);
            writeInt(stream, snapshot.numVessels);
            writeLong(stream, snapshot.randomSeed);
            writeLong(stream, snapshot.randomCounter);
            writeLong(stream, snapshot.currentTimeStep);
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
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);

            //Write fishEggs
            for (FishEgg egg : snapshot.fishEggs) {
                writeFishEgg(stream, egg);
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);

            //Write carcasses
            for (Carcass carcass : snapshot.carcasses) {
                writeCarcass(stream, carcass);
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);

            //Write vessels
            for (Vessel vessel : snapshot.vessels) {
                writeVessel(stream, vessel);
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);
        } catch (IOException e) {
            throw e;
        }
    }

    public static Snapshot loadSnapshot(String path) throws InvalidFormatException, IOException {
        Snapshot snapshot = new Snapshot();

        //Wrap stream in try-catch to handle automatic disposing of stream
        try(InputStream stream = new BufferedInputStream(Files.newInputStream(Paths.get(path)))) {
            //Read header containing metadata
            snapshot.width = readInt(stream);
            snapshot.height = readInt(stream);
            snapshot.numFish = readInt(stream);
            snapshot.numFishEgg = readInt(stream);
            snapshot.numCarcass = readInt(stream);
            snapshot.numVessels = readInt(stream);
            snapshot.randomSeed = readLong(stream);
            snapshot.randomCounter = readLong(stream);
            snapshot.currentTimeStep = readLong(stream); //Replace with "snapshot.currentTimeStep = 0;" for old snapshot files

            //Ensure we're at a separation between two different data blocks
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(1);
            }

            //Read plankton densities
            snapshot.planktonDensities = new int[snapshot.height][snapshot.width];
            for (int y = 0; y < snapshot.height; y++) {
                for (int x = 0; x < snapshot.width; x++) {
                    snapshot.planktonDensities[y][x] = readInt(stream);
                }
            }

            //Ensure we're at a separation between two different data blocks
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(2);
            }

            //Read fish
            snapshot.fish = new Fish[snapshot.numFish];
            for (int i = 0; i < snapshot.numFish; i++) {
                snapshot.fish[i] = readFish(stream);

                if (stream.read() != RECORD_SEPARATOR) {
                    throw new InvalidFormatException(21);
                }
            }

            //Ensure we're at a separation between two different data block
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(3);
            }

            //Read fish eggs
            snapshot.fishEggs = new FishEgg[snapshot.numFishEgg];
            for (int i = 0; i < snapshot.numFishEgg; i++) {
                snapshot.fishEggs[i] = readFishEgg(stream);

                if (stream.read() != RECORD_SEPARATOR) {
                    throw new InvalidFormatException(31);
                }
            }

            //Ensure we're at a separation between two different data blocks
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(4);
            }

            //Read carcasses
            snapshot.carcasses = new Carcass[snapshot.numCarcass];
            for (int i = 0; i < snapshot.numCarcass; i++) {
                snapshot.carcasses[i] = readCarcass(stream);

                if (stream.read() != RECORD_SEPARATOR) {
                    throw new InvalidFormatException(41);
                }
            }

            //Ensure we're at a separation between two different data blocks
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(5);
            }

            //Read vessels
            snapshot.vessels = new Vessel[snapshot.numVessels];
            for (int i = 0; i < snapshot.numVessels; i++) {
                snapshot.vessels[i] = readVessel(stream);

                if (stream.read() != RECORD_SEPARATOR) {
                    throw new InvalidFormatException(51);
                }
            }

            //Ensure we're at a separation between two different data block
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(6);
            }
        } catch (IOException e) {
            throw e;
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

    private static void writeBoolean(OutputStream stream, boolean value) throws IOException {
        stream.write(value ? 1 : 0);
    }

    private static void writeVector(OutputStream stream, Vector vec) throws IOException {
        writeInt(stream, vec.x);
        writeInt(stream, vec.y);
    }

    private static void writeFishGenome(OutputStream stream, FishGenome genome) throws IOException {

        //Write parent A genome
        float[] parentGenomeA = genome.getParentGenomeA().getArray();
        for (float value : parentGenomeA) {
            writeFloat(stream, value);
        }

        //Write parent B genome
        float[] parentGenomeB = genome.getParentGenomeB().getArray();
        for (float value : parentGenomeB) {
            writeFloat(stream, value);
        }

        //Write genome
        float[] genomeArray = genome.getArray();
        for (float value : genomeArray) {
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
        writeInt(stream, fish.getMatingTimer());
        writeBoolean(stream, fish.isMature());
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

    private static int readInt(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Integer.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(10);
        }

        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES).put(bytes);
        return buf.getInt(0);
    }

    private static long readLong(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Long.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(11);
        }

        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES).put(bytes);
        return buf.getLong(0);
    }

    private static float readFloat(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Float.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(12);
        }

        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES).put(bytes);
        return buf.getFloat(0);
    }

    private static double readDouble(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Double.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(13);
        }

        ByteBuffer buf = ByteBuffer.allocate(Double.BYTES).put(bytes);
        return buf.getDouble(0);
    }

    private static boolean readBoolean(InputStream stream) throws IOException, InvalidFormatException {
        int value = stream.read();

        if (value == -1) {
            throw new InvalidFormatException(14);
        }

        return value > 0;
    }

    private static Vector readVector(InputStream stream) throws IOException, InvalidFormatException {
        return new Vector(readInt(stream), readInt(stream));
    }

    private static FishGenome readFishGenome(InputStream stream) throws IOException, InvalidFormatException {
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

    private static Fish readFish(InputStream stream) throws IOException, InvalidFormatException {
        Vector position = readVector(stream);
        float health = readFloat(stream);
        float energy = readFloat(stream);
        float size = readFloat(stream);
        float speed = readFloat(stream);
        FishGenome genome = readFishGenome(stream);
        int matingTimer = readInt(stream);
        boolean isMature = readBoolean(stream);

        return new Fish(position, health, energy, size, speed, genome, matingTimer, isMature);
    }

    private static FishEgg readFishEgg(InputStream stream) throws IOException, InvalidFormatException {
        Vector position = readVector(stream);
        int numEggs = readInt(stream);
        int timeBeforeHatch = readInt(stream);
        FishGenome genome = readFishGenome(stream);

        return new FishEgg(position, numEggs, timeBeforeHatch, genome);
    }

    private static Carcass readCarcass(InputStream stream) throws IOException, InvalidFormatException {
        Vector position = readVector(stream);
        int nutrition = readInt(stream);

        return new Carcass(nutrition, position);
    }

    private static Vessel readVessel(InputStream stream) throws IOException, InvalidFormatException {
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

    //Getters

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

    public long getCurrentTimeStep() {
        return currentTimeStep;
    }
}