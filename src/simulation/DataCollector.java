package simulation;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import simulation.fields.Carcass;
import simulation.fields.Field;
import simulation.fields.Fish;
import simulation.fields.FishEgg;
import ui.ContentBox;
import ui.DragListener;

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
    private TextField timestepField = new TextField("0");
    private TextField bwdField = new TextField("0");
    private TextField morphologyField = new TextField("0");
    private TextField spawningField = new TextField("0");
    private TextField planktonField = new TextField("0");
    private TextField schoolingField = new TextField("0");
    private TextField fishField = new TextField("0");
    private TextField predatorField = new TextField("0");
    private TextField scavengerField = new TextField("0");
    private TextField planktivoreField = new TextField("0");
    private TextField eggField = new TextField("0");
    private TextField carcassField = new TextField("0");


    private String latestTimestep;
    private String latestBwd;
    private String latestMorphology;
    private String latestSpawning;
    private String latestPlankton;
    private String latestSchooling;
    private String latestFishCount;
    private String latestCarnivores;
    private String latestScavengers;
    private String latestPlanktivores;
    private String latestFishEggs;
    private String latestCarcasses;

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
            stream.write("Timestep,%BWD,Morphology,Spawn,Plankton,Schooling,Fish,Carnivores,Scavengers,Planktivores,Fish eggs,Carcasses\n");
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void append(SimulationSpace space, long timestep) {
        ArrayList<Fish> fish = getFish(space);
        latestTimestep = String.valueOf(timestep);
        latestBwd = String.valueOf(averageBWD(fish));
        latestMorphology = String.valueOf(averageMorphology(fish));
        latestSpawning = String.valueOf(averageMaxSpawning(fish));
        latestPlankton = String.valueOf(averagePlanktonDensity(space));
        latestSchooling = String.valueOf(averageSchoolingTendency(fish));
        latestFishCount = String.valueOf(fish.size());
        latestCarnivores = String.valueOf(carnivoreCount(fish));
        latestScavengers = String.valueOf(scavengerCount(fish));
        latestPlanktivores = String.valueOf(planktivoreCount(fish));
        latestFishEggs = String.valueOf(fishEggCount(space));
        latestCarcasses = String.valueOf(carcassCount(space));
        StringBuilder builder = new StringBuilder();

        builder.append(latestTimestep);
        builder.append(',');
        builder.append(latestBwd);
        builder.append(',');
        builder.append(latestMorphology);
        builder.append(',');
        builder.append(latestSpawning);
        builder.append(',');
        builder.append(latestPlankton);
        builder.append(',');
        builder.append(latestSchooling);
        builder.append(',');
        builder.append(latestFishCount);
        builder.append(',');
        builder.append(latestCarnivores);
        builder.append(',');
        builder.append(latestScavengers);
        builder.append(',');
        builder.append(latestPlanktivores);
        builder.append(',');
        builder.append(latestFishEggs);
        builder.append(',');
        builder.append(latestCarcasses);
        builder.append('\n');

        try {
            stream.write(builder.toString());
            --flushTimer;

            if (flushTimer == 0) {
                stream.flush();
                flushTimer = 100;
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void showLatestValues() {
        timestepField.setText(latestTimestep);
        bwdField.setText(latestBwd);
        morphologyField.setText(latestMorphology);
        spawningField.setText(latestSpawning);
        planktonField.setText(latestPlankton);
        schoolingField.setText(latestSchooling);
        fishField.setText(latestFishCount);
        predatorField.setText(latestCarnivores);
        scavengerField.setText(latestScavengers);
        planktivoreField.setText(latestPlanktivores);
        eggField.setText(latestFishEggs);
        carcassField.setText(latestCarcasses);
    }

    public void dispose() {
        try {
            stream.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private int carnivoreCount(ArrayList<Fish> fish) {
        int count = 0;

        for (Fish subject : fish) {
            if (subject.getGenome().getPredationTendency() >= 0.5f) {
                ++count;
            }
        }

        return count;
    }

    private int scavengerCount(ArrayList<Fish> fish) {
        int count = 0;

        for (Fish subject : fish) {
            if (subject.getGenome().getScavengeTendency() >= 0.5f) {
                ++count;
            }
        }

        return count;
    }

    private int planktivoreCount(ArrayList<Fish> fish) {
        int count = 0;

        for (Fish subject : fish) {
            if (subject.getGenome().getHerbivoreTendency() >= 0.5f) {
                ++count;
            }
        }

        return count;
    }

    private float averageBWD(ArrayList<Fish> fish) {
        float sum = 0;

        for (Fish subject : fish) {
            float energyConsumption = subject.getSize() * Settings.FISH_SIZE_PENALTY +
                    subject.getSpeed() * Settings.FISH_SPEED_PENALTY +
                    subject.getGenome().getHerbivoreEfficiency() * Settings.FISH_HERBIVORE_EFFICIENCY_PENALTY +
                    subject.getGenome().getCarnivoreEfficiency() * Settings.FISH_CARNIVORE_EFFICIENCY_PENALTY +
                    subject.getGenome().getAttackAbility() * Settings.FISH_ATTACK_ABILITY_PENALTY +
                    subject.getSize() * subject.getSpeed() * Settings.ENERGY_SPEED_CORRELATION;

            sum += energyConsumption / Math.pow((subject.getSize() * Settings.MAX_FISH_SIZE), 3) * 100;
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
                sum += space.getTile(x, y).getMuDensity() / Settings.MAX_PLANKTON;
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
            sum += Settings.MATING_ENERGY_CONSUMPTION * subject.getSize();
        }

        return sum / fish.size();
    }

    private double averageMorphology(ArrayList<Fish> fish) {
        double sum = 0;

        for (Fish subject : fish) {
            sum += subject.getSize() * Settings.MAX_FISH_SIZE;
        }

        return sum / fish.size();
    }

    public ContentBox getStatisticsUI(double width, DragListener dragListener) {
        ContentBox contentBox = new ContentBox("Statistics", width, dragListener);
        VBox content = new VBox(1);

        content.setFillWidth(true);
        content.getChildren().addAll(
                getLine("Timestep:", timestepField),
                getLine("Average %BWD:", bwdField),
                getLine("Average morphology:", morphologyField),
                getLine("Average spawn:", spawningField),
                getLine("Plankton density:", planktonField),
                getLine("Average schooling:", schoolingField),
                getLine("Fish population:", fishField),
                getLine("Carnivore population:", predatorField),
                getLine("Scavenger population:", scavengerField),
                getLine("Planktivore population:", planktivoreField),
                getLine("Fish egg population:", eggField),
                getLine("Carcass population:", carcassField));

        contentBox.setContent(content);

        return contentBox;
    }

    private HBox getLine(String label, TextField field) {
        HBox line = new HBox(2);

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        line.getChildren().addAll(new Label(label), spacer, field);

        return line;
    }
}
