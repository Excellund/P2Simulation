package simulation;

import simulation.Subjects.Carcass;
import simulation.Subjects.Field;
import simulation.Subjects.Fish;
import simulation.Subjects.FishEgg;
import utils.CountingRandom;
import utils.Vector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Snapshot {
    private static final byte GROUP_SEPARATOR = 0x1D;

    public static boolean saveSnapshot(String path, Simulation sim) {
        int width = sim.getSpace().getWidth();
        int height = sim.getSpace().getHeight();
        int numFish = 0, numFishEgg = 0, numCarcass = 0;
        int numVessels = sim.getVessels().size();
        long randomCounter = CountingRandom.getInstance().getCounter();

        //Get number of Fish, FishEgg, Carcass
        for (Field field : sim.getSpace()) {
            if (field instanceof Fish) { numFish++; }
            else if (field instanceof FishEgg) { numFishEgg++; }
            else if (field instanceof Carcass) { numCarcass++; }
        }

        Fish[] fishes = new Fish[numFish];
        FishEgg[] fishEggs = new FishEgg[numFishEgg];
        Carcass[] carcasses = new Carcass[numCarcass];

        int count = 0;
        for (Field field : sim.getSpace()) {
            if (field instanceof Fish) {
                fishes[count] = (Fish) field;
            }
            else if (field instanceof FishEgg) {
                fishEggs[count] = (FishEgg) field;
            }
            else if (field instanceof Carcass) {
                carcasses[count] = (Carcass) field;
            }

            count++;
        }

        try (OutputStream stream = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)))) {
            //Write header containing metadata
            writeInt(stream, width);
            writeInt(stream, height);
            writeInt(stream, numFish);
            writeInt(stream, numFishEgg);
            writeInt(stream, numCarcass);
            writeInt(stream, numVessels);
            writeLong(stream, randomCounter);
            stream.write(GROUP_SEPARATOR);

            //Write plankton densities
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    writeInt(stream, sim.getSpace().getTile(x, y).getMuDensity());
                }
            }
            stream.write(GROUP_SEPARATOR);

            //Write fishes
            for (Fish fish : fishes) {
                writeFish(stream, fish);
            }
            stream.write(GROUP_SEPARATOR);

            //Write fishEggs
            for (FishEgg egg : fishEggs) {
                writeFishEgg(stream, egg);
            }
            stream.write(GROUP_SEPARATOR);

            //Write carcasses
            for (Carcass carcass : carcasses) {
                writeCarcass(stream, carcass);
            }
            stream.write(GROUP_SEPARATOR);

            //Write vessels
            for (Vessel vessel : sim.getVessels()) {
                writeVessel(stream, vessel);
            }
            stream.write(GROUP_SEPARATOR);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

    private static void writeVector(OutputStream stream, Vector vec) throws IOException {
        writeInt(stream, vec.x);
        writeInt(stream, vec.y);
    }

    private static void writeFishGenome(OutputStream stream, FishGenome genome) throws IOException {
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
        writeFishGenome(stream, fish.getGenome().getParentGenomeA());
        writeFishGenome(stream, fish.getGenome().getParentGenomeB());
    }

    private static void writeFishEgg(OutputStream stream, FishEgg egg) throws IOException {
        writeVector(stream, egg.getPosition());
        writeInt(stream, egg.getNumEggs());
        writeInt(stream, egg.getTimeBeforeHatch());
        writeFishGenome(stream, egg.getGenome());
        writeFishGenome(stream, egg.getGenome().getParentGenomeA());
        writeFishGenome(stream, egg.getGenome().getParentGenomeB());
    }

    private static void writeCarcass(OutputStream stream, Carcass carcass) throws IOException {
        writeVector(stream, carcass.getPosition());
        writeInt(stream, carcass.getNutrition());
    }

    private static void writeVessel(OutputStream stream, Vessel vessel) {
        //TODO: implement
    }

    public static Simulation loadSnapshot(String path) {
        return null;
    }
}
