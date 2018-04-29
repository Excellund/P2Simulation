package simulation;

import simulation.fields.Carcass;
import simulation.fields.Field;
import simulation.fields.Fish;
import simulation.fields.FishEgg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataCollector {
    private BufferedWriter stream;
    private int flushTimer;

    public DataCollector() {
        flushTimer = 100;

        int count = 1;

        try {
            if (!Files.exists(Paths.get("output/"))) {
                Files.createDirectory(Paths.get("output/"));
            }

            while (Files.exists(Paths.get("output/data_" + count + ".txt"))) {
                ++count;
            }

            stream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output/data_" + count + ".txt"), "utf-8"));
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void append(SimulationSpace space, long timestep) {
        ArrayList<Fish> fish = getFish(space);
        String line = String.valueOf(timestep) + ',';

        line += String.valueOf(averageBWD(fish));
        line += ',';
        line += String.valueOf(averageMorphology(fish));
        line += ',';
        line += String.valueOf(averagePlanktonDensity(space));
        line += ',';
        line += String.valueOf(fish.size());
        line += ',';
        line += String.valueOf(carcassCount(space));
        line += ',';
        line += String.valueOf(fishEggCount(space));
        line += ',';
        line += String.valueOf(averageMaxSpawning(fish));
        line += ',';
        line += String.valueOf(averageSchoolingTendency(fish));
        line += '\n';

        try {
            stream.write(line);
            --flushTimer;

            if (flushTimer == 0) {
                stream.flush();
                flushTimer = 100;
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void dispose() {
        try {
            stream.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private float averageBWD(ArrayList<Fish> fish) {
        float sum = 0;

        for (Fish subject : fish) {
            sum += subject.getSize() * subject.getSpeed();
        }

        return sum / fish.size();
    }

    private long carcassCount(SimulationSpace space) {
        long count = 0;

        for (Field field : space) {
            if (field instanceof Carcass) {
                ++count;
            }
        }

        return count;
    }

    private long fishEggCount(SimulationSpace space) {
        long count = 0;

        for (Field field : space) {
            if (field instanceof FishEgg) {
                ++count;
            }
        }

        return count;
    }

    private double averagePlanktonDensity(SimulationSpace space) {
        double sum = 0;

        for (int y = 0; y < space.getHeight(); ++y) {
            for (int x = 0; x < space.getWidth(); ++x) {
                sum += space.getTile(x, y).getMuDensity();
            }
        }

        return sum / (space.getHeight() * space.getWidth());
    }

    private ArrayList<Fish> getFish(SimulationSpace space) {
        ArrayList<Fish> fish = new ArrayList<>();

        for (Field field : space) {
            if (field instanceof Fish) {
                fish.add((Fish) field);
            }
        }

        return fish;
    }

    private double averageSchoolingTendency(ArrayList<Fish> fish) {
        double sum = 0;

        for (Fish subject : fish) {
            sum += subject.getGenome().getSchoolingTendency();
        }

        return sum / fish.size();
    }

    private double averageMaxSpawning(ArrayList<Fish> fish) {
        double sum = 0;

        for (Fish subject : fish) {
            sum += subject.getSize() * Settings.MAX_FISH_SIZE;
        }

        return sum / fish.size();
    }

    private double averageMorphology(ArrayList<Fish> fish) {
        double sum = 0;

        for (Fish subject : fish) {
            sum += subject.getSize();
        }

        return sum / fish.size();
    }
}
