package simulation;

import simulation.exceptions.InvalidFormatException;
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
import java.util.Arrays;
import java.util.Objects;

public class Snapshot {
    private static final byte GROUP_SEPARATOR = 0x1D;
    private static final byte RECORD_SEPARATOR = 0x1E;
    private static final byte FIELD_FISH_SPECIFIER = 0x01;
    private static final byte FIELD_FISH_EGG_SPECIFIER = 0x02;
    private static final byte FIELD_CARCASS_SPECIFIER = 0x03;

    private int width, height;
    private int numFields, numVessels;
    private long randomSeed;
    private long randomCounter;
    private long currentTimeStep;

    private Field[] fields;
    private Vessel[] vessels;

    private int[][] planktonDensities;

    private Snapshot() {

    }

    public Snapshot(Simulation sim) {
        width = sim.getSpace().getWidth();
        height = sim.getSpace().getHeight();
        numVessels = sim.getVessels().size();
        randomSeed = CountingRandom.getInstance().getInitialSeed();
        randomCounter = CountingRandom.getInstance().getCounter();
        currentTimeStep = sim.getCurrentTimeStep();

        numFields = sim.getSpace().getNumActiveFields();

        //Assign space to the Fish, FishEggs, Carcasses
        fields = new Field[numFields];
        vessels = new Vessel[sim.getVessels().size()];

        //Populate the array
        for (int i = 0; i < numFields; i++) {
            fields[i] = sim.getSpace().getActiveFields().get(i);
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
        try (OutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)))) {
            //Write header containing metadata
            writeInt(stream, snapshot.width);
            writeInt(stream, snapshot.height);
            writeInt(stream, snapshot.numFields);
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

            //Write fields
            for (Field field : snapshot.fields) {
                writeField(stream, field);
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);

            //Write vessels
            for (Vessel vessel : snapshot.vessels) {
                writeVessel(stream, vessel);
                stream.write(RECORD_SEPARATOR);
            }
            stream.write(GROUP_SEPARATOR);
        }
    }

    public static Snapshot loadSnapshot(String path) throws InvalidFormatException, IOException {
        Snapshot snapshot = new Snapshot();

        //Wrap stream in try-catch to handle automatic disposing of stream
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(Paths.get(path)))) {
            //Read header containing metadata
            snapshot.width = readInt(stream);
            snapshot.height = readInt(stream);
            snapshot.numFields = readInt(stream);
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

            //Read fields
            snapshot.fields = new Field[snapshot.numFields];
            for (int i = 0; i < snapshot.numFields; i++) {
                snapshot.fields[i] = readField(stream);

                if (stream.read() != RECORD_SEPARATOR) {
                    throw new InvalidFormatException(21);
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

            //Ensure we're at a separation between two different data blocks
            if (stream.read() != GROUP_SEPARATOR) {
                throw new InvalidFormatException(6);
            }
        }

        return snapshot;
    }

    //Writes a int to a stream
    private static void writeInt(OutputStream stream, int value) throws IOException {
        stream.write(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    //Writes a long to a stream
    private static void writeLong(OutputStream stream, long value) throws IOException {
        stream.write(ByteBuffer.allocate(Long.BYTES).putLong(value).array());
    }

    //Writes a float to a stream
    private static void writeFloat(OutputStream stream, float value) throws IOException {
        stream.write(ByteBuffer.allocate(Float.BYTES).putFloat(value).array());
    }

    //Writes a double to a stream
    private static void writeDouble(OutputStream stream, double value) throws IOException {
        stream.write(ByteBuffer.allocate(Double.BYTES).putDouble(value).array());
    }

    //Writes a boolean to a stream
    private static void writeBoolean(OutputStream stream, boolean value) throws IOException {
        stream.write(value ? 1 : 0);
    }

    //Writes a Vector to a stream
    private static void writeVector(OutputStream stream, Vector vec) throws IOException {
        writeInt(stream, vec.x);
        writeInt(stream, vec.y);
    }

    //Writes a FishGenome to a stream
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

    //Writes a Fish to a stream
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

    //Writes a FishEgg to a stream
    private static void writeFishEgg(OutputStream stream, FishEgg egg) throws IOException {
        writeVector(stream, egg.getPosition());
        writeInt(stream, egg.getNumEggs());
        writeInt(stream, egg.getTimeBeforeHatch());
        writeFishGenome(stream, egg.getGenome());
    }

    //Writes a Carcass to a stream
    private static void writeCarcass(OutputStream stream, Carcass carcass) throws IOException {
        writeVector(stream, carcass.getPosition());
        writeInt(stream, carcass.getNutrition());
    }

    private static void writeField(OutputStream stream, Field field) throws IOException {
        if (field instanceof Fish) {
            stream.write(FIELD_FISH_SPECIFIER);
            writeFish(stream, (Fish) field);
        } else if (field instanceof FishEgg) {
            stream.write(FIELD_FISH_EGG_SPECIFIER);
            writeFishEgg(stream, (FishEgg) field);
        } else if (field instanceof Carcass) {
            stream.write(FIELD_CARCASS_SPECIFIER);
            writeCarcass(stream, (Carcass) field);
        }
    }

    //Writes a vessel to a stream
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

    //Reads a int from a stream
    private static int readInt(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Integer.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(10);
        }

        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES).put(bytes);
        return buf.getInt(0);
    }

    //Reads a long from a stream
    private static long readLong(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Long.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(11);
        }

        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES).put(bytes);
        return buf.getLong(0);
    }

    //Reads a float from a stream
    private static float readFloat(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Float.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(12);
        }

        ByteBuffer buf = ByteBuffer.allocate(Float.BYTES).put(bytes);
        return buf.getFloat(0);
    }

    //Reads a double from a stream
    private static double readDouble(InputStream stream) throws IOException, InvalidFormatException {
        byte[] bytes = new byte[Double.BYTES];

        if (stream.read(bytes) == -1) {
            throw new InvalidFormatException(13);
        }

        ByteBuffer buf = ByteBuffer.allocate(Double.BYTES).put(bytes);
        return buf.getDouble(0);
    }

    //Reads a boolean from a stream
    private static boolean readBoolean(InputStream stream) throws IOException, InvalidFormatException {
        int value = stream.read();

        if (value == -1) {
            throw new InvalidFormatException(14);
        }

        return value > 0;
    }

    //Reads a Vector from a stream
    private static Vector readVector(InputStream stream) throws IOException, InvalidFormatException {
        return new Vector(readInt(stream), readInt(stream));
    }

    //Reads a FishGenome from a stream
    private static FishGenome readFishGenome(InputStream stream) throws IOException, InvalidFormatException {
        float[] attributesParentA = new float[FishGenome.NUM_ATTRIBUTES];
        float[] attributesParentB = new float[FishGenome.NUM_ATTRIBUTES];
        float[] attributes = new float[FishGenome.NUM_ATTRIBUTES];

        //Read parent a genome
        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributesParentA[i] = readFloat(stream);
        }

        //Read parent b genome
        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributesParentB[i] = readFloat(stream);
        }

        //Read genome
        for (int i = 0; i < FishGenome.NUM_ATTRIBUTES; i++) {
            attributes[i] = readFloat(stream);
        }

        FishGenome parentA = new FishGenome(attributesParentA, null, null);
        FishGenome parentB = new FishGenome(attributesParentB, null, null);
        return new FishGenome(attributes, parentA, parentB);
    }

    //Reads a Fish from a stream
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

    //Reads a FishEgg from a stream
    private static FishEgg readFishEgg(InputStream stream) throws IOException, InvalidFormatException {
        Vector position = readVector(stream);
        int numEggs = readInt(stream);
        int timeBeforeHatch = readInt(stream);
        FishGenome genome = readFishGenome(stream);

        return new FishEgg(position, numEggs, timeBeforeHatch, genome);
    }

    //Reads a Carcass from a stream
    private static Carcass readCarcass(InputStream stream) throws IOException, InvalidFormatException {
        Vector position = readVector(stream);
        int nutrition = readInt(stream);

        return new Carcass(nutrition, position);
    }

    private static Field readField(InputStream stream) throws IOException, InvalidFormatException {
        int fieldSpecifier = stream.read();

        if (fieldSpecifier == -1) {
            throw new InvalidFormatException(7);
        }

        switch (fieldSpecifier) {
            case FIELD_FISH_SPECIFIER:
                return readFish(stream);
            case FIELD_FISH_EGG_SPECIFIER:
                return readFishEgg(stream);
            case FIELD_CARCASS_SPECIFIER:
                return readCarcass(stream);
        }

        return null;
    }


    //Reads a Vessel from a stream
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

    public Field[] getFields() {
        return fields;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snapshot snapshot = (Snapshot) o;
        return width == snapshot.width &&
                height == snapshot.height &&
                numFields == snapshot.numFields &&
                numVessels == snapshot.numVessels &&
                randomSeed == snapshot.randomSeed &&
                randomCounter == snapshot.randomCounter &&
                currentTimeStep == snapshot.currentTimeStep &&
                Arrays.equals(fields, snapshot.fields) &&
                Arrays.equals(vessels, snapshot.vessels) &&
                Arrays.equals(planktonDensities, snapshot.planktonDensities);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(width, height, numFields, numVessels, randomSeed, randomCounter, currentTimeStep);
        result = 31 * result + Arrays.hashCode(fields);
        result = 31 * result + Arrays.hashCode(vessels);
        result = 31 * result + Arrays.hashCode(planktonDensities);
        return result;
    }
}

