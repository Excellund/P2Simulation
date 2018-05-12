package simulation;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Settings {
    //Simulation settings
    public static float PLANKTON_GROWTH_PER_TIMESTEP;
    public static float NUM_VESSELS;
    public static float MAX_PLANKTON;
    public static float INITIAL_MAX_PLANKTON_DENSITY;
    public static float DATACOLLECTOR_APPEND_DELAY;

    //Fish settings
    public static float MAX_FISH_SIZE;
    public static float ENERGY_PER_EGG;
    public static float MIN_COMPATIBILITY_MATING;
    public static float MIN_PREDATION_TENDENCY;
    public static float MAX_ATTACK_DAMAGE;
    public static float ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
    public static float COMPATIBILITY_STEEPNESS;
    public static float COMPATIBILITY_MIDPOINT;
    public static float EXPECTED_MUTATION_AMOUNT;
    public static float MUTATION_GAUSSIAN_VARIANCE;
    public static float NUTRITION_PER_SIZE_POINT;
    public static float MIN_ENERGY_MATING;
    public static float MATING_ENERGY_CONSUMPTION;
    public static float HEALTH_POINTS_PER_SIZE_POINTS;
    public static float ENERGY_POINTS_PER_SIZE_POINTS;
    public static float HEALTH_REDUCTION_ON_LOW_ENERGY;
    public static float MIN_ENERGY_HEALTH_INCREASE;
    public static float ENERGY_HEALTH_INCREASE;
    public static float TIME_BEFORE_HATCH;
    public static float CARCASS_DECAY_PER_TIMESTEP;
    public static float MAX_MOVES_CORRESPONDING_TO_SPEED;
    public static float ENERGY_SPEED_CORRELATION;
    public static float MATING_DELAY;
    public static float VISION_RANGE;
    public static float FISH_GROWTH_RATE_PER_TIMESTEP;
    public static float FISH_SIZE_PENALTY;
    public static float FISH_SPEED_PENALTY;
    public static float FISH_HERBIVORE_EFFICIENCY_PENALTY;
    public static float FISH_CARNIVORE_EFFICIENCY_PENALTY;
    public static float FISH_ATTACK_ABILITY_PENALTY;
    public static float COLOR_BY_TENDENCY;

    //Fishing settings
    public static float MAX_MORPHOLOGY;
    public static float MIN_MORPHOLOGY;
    public static float FISHING_QUOTAS_MIN;
    public static float FISHING_QUOTAS_MAX;
    public static float VESSEL_TRAVEL_DISTANCE;
    public static float WIDTH_STEEPNESS;
    public static float VESSEL_SCALE;
    //Spawn Vessel
    public static float MORPHOLOGY;
    public static float QUOTAS;
    //Spawn Plankton
    public static float ADD_PLANKTON;
    //Launch settings
    public static float NUM_INITIAL_FISH;
    public static float LOAD_PLANKTON;
    //Graphics settings
    public static float PLANKTON_GAMMA;
    public static float FISH_GAMMA;
    public static float TARGET_FPS;

    private static Map<String, Float> abbreviated = new HashMap<>();

    public static void defaultAbbreviated() {
        abbreviated.put("PLANKTON_GROWTH_PER_TIMESTEP", 1300f);
        abbreviated.put("NUM_VESSELS", 3f);
        abbreviated.put("MAX_PLANKTON", 140000f);
        abbreviated.put("INITIAL_MAX_PLANKTON_DENSITY", 300000f);
        abbreviated.put("DATACOLLECTOR_APPEND_DELAY", 100f);

        abbreviated.put("MAX_FISH_SIZE", 100f);
        abbreviated.put("ENERGY_PER_EGG", 3f);
        abbreviated.put("MIN_COMPATIBILITY_MATING", 0.8f);
        abbreviated.put("MIN_PREDATION_TENDENCY", 0.2f);
        abbreviated.put("MAX_ATTACK_DAMAGE", 20f);
        abbreviated.put("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE", 0.02f);
        abbreviated.put("COMPATIBILITY_STEEPNESS", 40f);
        abbreviated.put("COMPATIBILITY_MIDPOINT", 0.75f);
        abbreviated.put("EXPECTED_MUTATION_AMOUNT", 8f);
        abbreviated.put("MUTATION_GAUSSIAN_VARIANCE", 0.30f);
        abbreviated.put("NUTRITION_PER_SIZE_POINT", 0.01f);
        abbreviated.put("MIN_ENERGY_MATING", 90f);
        abbreviated.put("MATING_ENERGY_CONSUMPTION", 140f);
        abbreviated.put("HEALTH_POINTS_PER_SIZE_POINTS", 1f);
        abbreviated.put("ENERGY_POINTS_PER_SIZE_POINTS", 2f);
        abbreviated.put("HEALTH_REDUCTION_ON_LOW_ENERGY", 10f);
        abbreviated.put("MIN_ENERGY_HEALTH_INCREASE", 80f);
        abbreviated.put("ENERGY_HEALTH_INCREASE", 5f);
        abbreviated.put("TIME_BEFORE_HATCH", 50f);
        abbreviated.put("CARCASS_DECAY_PER_TIMESTEP", 1f);
        abbreviated.put("MAX_MOVES_CORRESPONDING_TO_SPEED", 3f);
        abbreviated.put("ENERGY_SPEED_CORRELATION", 90f);
        abbreviated.put("MATING_DELAY", 600f);
        abbreviated.put("VISION_RANGE", 3f);
        abbreviated.put("FISH_GROWTH_RATE_PER_TIMESTEP", 0.03f);
        abbreviated.put("FISH_SIZE_PENALTY", 2.0f); //TODO: UI
        abbreviated.put("FISH_SPEED_PENALTY", 2.0f); //TODO: UI
        abbreviated.put("FISH_HERBIVORE_EFFICIENCY_PENALTY", 10.0f); //TODO: UI
        abbreviated.put("FISH_CARNIVORE_EFFICIENCY_PENALTY", 2.0f); //TODO: UI
        abbreviated.put("FISH_ATTACK_ABILITY_PENALTY", 2.0f); //TODO: UI
        abbreviated.put("COLOR_BY_TENDENCY", 1.0f);

        abbreviated.put("MAX_MORPHOLOGY", 0.8f);
        abbreviated.put("MIN_MORPHOLOGY", 0.1f);
        abbreviated.put("FISHING_QUOTAS_MIN", 300f);
        abbreviated.put("FISHING_QUOTAS_MAX", 3000f);
        abbreviated.put("VESSEL_TRAVEL_DISTANCE", 600f);
        abbreviated.put("WIDTH_STEEPNESS", 20f);
        abbreviated.put("VESSEL_SCALE", 2f);

        abbreviated.put("MORPHOLOGY", 0.5f);
        abbreviated.put("QUOTAS", 1500f);

        abbreviated.put("ADD_PLANKTON", 100000f);

        abbreviated.put("NUM_INITIAL_FISH", 3000f);
        abbreviated.put("LOAD_PLANKTON", 200000f);

        abbreviated.put("PLANKTON_GAMMA", 1.0f);
        abbreviated.put("FISH_GAMMA", 1.0f);
        abbreviated.put("TARGET_FPS", 7.0f);
    }

    public static void useAbbreviated() {
        PLANKTON_GROWTH_PER_TIMESTEP = abbreviated.get("PLANKTON_GROWTH_PER_TIMESTEP");
        NUM_VESSELS = abbreviated.get("NUM_VESSELS");
        MAX_PLANKTON = abbreviated.get("MAX_PLANKTON");
        INITIAL_MAX_PLANKTON_DENSITY = abbreviated.get("INITIAL_MAX_PLANKTON_DENSITY");
        DATACOLLECTOR_APPEND_DELAY = abbreviated.get("DATACOLLECTOR_APPEND_DELAY");

        MAX_FISH_SIZE = abbreviated.get("MAX_FISH_SIZE");
        ENERGY_PER_EGG = abbreviated.get("ENERGY_PER_EGG");
        MIN_COMPATIBILITY_MATING = abbreviated.get("MIN_COMPATIBILITY_MATING");
        MIN_PREDATION_TENDENCY = abbreviated.get("MIN_PREDATION_TENDENCY");
        MAX_ATTACK_DAMAGE = abbreviated.get("MAX_ATTACK_DAMAGE");
        ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE = abbreviated.get("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE");
        COMPATIBILITY_STEEPNESS = abbreviated.get("COMPATIBILITY_STEEPNESS");
        COMPATIBILITY_MIDPOINT = abbreviated.get("COMPATIBILITY_MIDPOINT");
        EXPECTED_MUTATION_AMOUNT = abbreviated.get("EXPECTED_MUTATION_AMOUNT");
        MUTATION_GAUSSIAN_VARIANCE = abbreviated.get("MUTATION_GAUSSIAN_VARIANCE");
        NUTRITION_PER_SIZE_POINT = abbreviated.get("NUTRITION_PER_SIZE_POINT");
        MIN_ENERGY_MATING = abbreviated.get("MIN_ENERGY_MATING");
        MATING_ENERGY_CONSUMPTION = abbreviated.get("MATING_ENERGY_CONSUMPTION");
        HEALTH_POINTS_PER_SIZE_POINTS = abbreviated.get("HEALTH_POINTS_PER_SIZE_POINTS");
        ENERGY_POINTS_PER_SIZE_POINTS = abbreviated.get("ENERGY_POINTS_PER_SIZE_POINTS");
        HEALTH_REDUCTION_ON_LOW_ENERGY = abbreviated.get("HEALTH_REDUCTION_ON_LOW_ENERGY");
        MIN_ENERGY_HEALTH_INCREASE = abbreviated.get("MIN_ENERGY_HEALTH_INCREASE");
        ENERGY_HEALTH_INCREASE = abbreviated.get("ENERGY_HEALTH_INCREASE");
        TIME_BEFORE_HATCH = abbreviated.get("TIME_BEFORE_HATCH");
        CARCASS_DECAY_PER_TIMESTEP = abbreviated.get("CARCASS_DECAY_PER_TIMESTEP");
        MAX_MOVES_CORRESPONDING_TO_SPEED = abbreviated.get("MAX_MOVES_CORRESPONDING_TO_SPEED");
        ENERGY_SPEED_CORRELATION = abbreviated.get("ENERGY_SPEED_CORRELATION");
        MATING_DELAY = abbreviated.get("MATING_DELAY");
        VISION_RANGE = abbreviated.get("VISION_RANGE");
        FISH_GROWTH_RATE_PER_TIMESTEP = abbreviated.get("FISH_GROWTH_RATE_PER_TIMESTEP");
        FISH_SIZE_PENALTY = abbreviated.get("FISH_SIZE_PENALTY");
        FISH_SPEED_PENALTY = abbreviated.get("FISH_SPEED_PENALTY");
        FISH_HERBIVORE_EFFICIENCY_PENALTY = abbreviated.get("FISH_HERBIVORE_EFFICIENCY_PENALTY");
        FISH_CARNIVORE_EFFICIENCY_PENALTY = abbreviated.get("FISH_CARNIVORE_EFFICIENCY_PENALTY");
        FISH_ATTACK_ABILITY_PENALTY = abbreviated.get("FISH_ATTACK_ABILITY_PENALTY");
        FISH_SIZE_PENALTY = abbreviated.get("FISH_SIZE_PENALTY");
        COLOR_BY_TENDENCY = abbreviated.get("COLOR_BY_TENDENCY");

        MAX_MORPHOLOGY = abbreviated.get("MAX_MORPHOLOGY");
        MIN_MORPHOLOGY = abbreviated.get("MIN_MORPHOLOGY");
        FISHING_QUOTAS_MIN = abbreviated.get("FISHING_QUOTAS_MIN");
        FISHING_QUOTAS_MAX = abbreviated.get("FISHING_QUOTAS_MAX");
        VESSEL_TRAVEL_DISTANCE = abbreviated.get("VESSEL_TRAVEL_DISTANCE");
        WIDTH_STEEPNESS = abbreviated.get("WIDTH_STEEPNESS");
        VESSEL_SCALE = abbreviated.get("VESSEL_SCALE");

        MORPHOLOGY = abbreviated.get("MORPHOLOGY");
        QUOTAS = abbreviated.get("QUOTAS");

        ADD_PLANKTON = abbreviated.get("ADD_PLANKTON");

        NUM_INITIAL_FISH = abbreviated.get("NUM_INITIAL_FISH");
        LOAD_PLANKTON = abbreviated.get("LOAD_PLANKTON");

        PLANKTON_GAMMA = abbreviated.get("PLANKTON_GAMMA");
        FISH_GAMMA = abbreviated.get("FISH_GAMMA");
        TARGET_FPS = abbreviated.get("TARGET_FPS");

    }

    public static void getAbbreviated() {
        abbreviated.put("PLANKTON_GROWTH_PER_TIMESTEP", PLANKTON_GROWTH_PER_TIMESTEP);
        abbreviated.put("NUM_VESSELS", NUM_VESSELS);
        abbreviated.put("MAX_PLANKTON", MAX_PLANKTON);
        abbreviated.put("INITIAL_MAX_PLANKTON_DENSITY", INITIAL_MAX_PLANKTON_DENSITY);
        abbreviated.put("DATACOLLECTOR_APPEND_DELAY", DATACOLLECTOR_APPEND_DELAY);

        abbreviated.put("MAX_FISH_SIZE", MAX_FISH_SIZE);
        abbreviated.put("ENERGY_PER_EGG", ENERGY_PER_EGG);
        abbreviated.put("MIN_COMPATIBILITY_MATING", MIN_COMPATIBILITY_MATING);
        abbreviated.put("MIN_PREDATION_TENDENCY", MIN_PREDATION_TENDENCY);
        abbreviated.put("MAX_ATTACK_DAMAGE", MAX_ATTACK_DAMAGE);
        abbreviated.put("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE", ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE);
        abbreviated.put("COMPATIBILITY_STEEPNESS", COMPATIBILITY_STEEPNESS);
        abbreviated.put("COMPATIBILITY_MIDPOINT", COMPATIBILITY_MIDPOINT);
        abbreviated.put("EXPECTED_MUTATION_AMOUNT", EXPECTED_MUTATION_AMOUNT);
        abbreviated.put("MUTATION_GAUSSIAN_VARIANCE", MUTATION_GAUSSIAN_VARIANCE);
        abbreviated.put("NUTRITION_PER_SIZE_POINT", NUTRITION_PER_SIZE_POINT);
        abbreviated.put("MIN_ENERGY_MATING", MIN_ENERGY_MATING);
        abbreviated.put("MATING_ENERGY_CONSUMPTION", MATING_ENERGY_CONSUMPTION);
        abbreviated.put("HEALTH_POINTS_PER_SIZE_POINTS", HEALTH_POINTS_PER_SIZE_POINTS);
        abbreviated.put("ENERGY_POINTS_PER_SIZE_POINTS", ENERGY_POINTS_PER_SIZE_POINTS);
        abbreviated.put("HEALTH_REDUCTION_ON_LOW_ENERGY", HEALTH_REDUCTION_ON_LOW_ENERGY);
        abbreviated.put("MIN_ENERGY_HEALTH_INCREASE", MIN_ENERGY_HEALTH_INCREASE);
        abbreviated.put("ENERGY_HEALTH_INCREASE", ENERGY_HEALTH_INCREASE);
        abbreviated.put("TIME_BEFORE_HATCH", TIME_BEFORE_HATCH);
        abbreviated.put("CARCASS_DECAY_PER_TIMESTEP", CARCASS_DECAY_PER_TIMESTEP);
        abbreviated.put("MAX_MOVES_CORRESPONDING_TO_SPEED", MAX_MOVES_CORRESPONDING_TO_SPEED);
        abbreviated.put("ENERGY_SPEED_CORRELATION", ENERGY_SPEED_CORRELATION);
        abbreviated.put("MATING_DELAY", MATING_DELAY);
        abbreviated.put("VISION_RANGE", VISION_RANGE);
        abbreviated.put("FISH_GROWTH_RATE_PER_TIMESTEP", FISH_GROWTH_RATE_PER_TIMESTEP);
        abbreviated.put("FISH_SIZE_PENALTY", FISH_SIZE_PENALTY);
        abbreviated.put("FISH_SPEED_PENALTY", FISH_SPEED_PENALTY);
        abbreviated.put("FISH_HERBIVORE_EFFICIENCY_PENALTY", FISH_HERBIVORE_EFFICIENCY_PENALTY);
        abbreviated.put("FISH_CARNIVORE_EFFICIENCY_PENALTY", FISH_CARNIVORE_EFFICIENCY_PENALTY);
        abbreviated.put("FISH_ATTACK_ABILITY_PENALTY", FISH_ATTACK_ABILITY_PENALTY);
        abbreviated.put("COLOR_BY_TENDENCY", COLOR_BY_TENDENCY);

        abbreviated.put("MAX_MORPHOLOGY", MAX_MORPHOLOGY);
        abbreviated.put("MIN_MORPHOLOGY", MIN_MORPHOLOGY);
        abbreviated.put("FISHING_QUOTAS_MIN", FISHING_QUOTAS_MIN);
        abbreviated.put("FISHING_QUOTAS_MAX", FISHING_QUOTAS_MAX);
        abbreviated.put("VESSEL_TRAVEL_DISTANCE", VESSEL_TRAVEL_DISTANCE);
        abbreviated.put("WIDTH_STEEPNESS", WIDTH_STEEPNESS);
        abbreviated.put("VESSEL_SCALE", VESSEL_SCALE);

        abbreviated.put("MORPHOLOGY", MORPHOLOGY);
        abbreviated.put("QUOTAS", QUOTAS);

        abbreviated.put("ADD_PLANKTON", ADD_PLANKTON);

        abbreviated.put("NUM_INITIAL_FISH", NUM_INITIAL_FISH);
        abbreviated.put("LOAD_PLANKTON", LOAD_PLANKTON);

        abbreviated.put("PLANKTON_GAMMA", PLANKTON_GAMMA);
        abbreviated.put("FISH_GAMMA", FISH_GAMMA);
        abbreviated.put("TARGET_FPS", TARGET_FPS);
    }

    public static void toFile(String name) {
        Path path = Paths.get("settings/");

        getAbbreviated();

        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("settings/" + name + ".profile"), "utf-8"))) {
            for (String key : abbreviated.keySet()) {
                writer.write(String.format(Locale.ROOT, "%s=%f\n", key, abbreviated.get(key)));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void fromFile(String name) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("settings/" + name + ".profile"), "utf-8"))) {
            while (reader.ready()) {
                String[] elements = reader.readLine().split("=");

                if (abbreviated.containsKey(elements[0])) {
                    abbreviated.replace(elements[0], Float.parseFloat(elements[1]));
                } else {
                    abbreviated.put(elements[0], Float.parseFloat(elements[1]));
                }
            }

            useAbbreviated();
        } catch (IOException e) {
            System.out.println(e.getMessage()); //file doesn't exist
        }
    }

    public static ArrayList<String> getFiles() {
        Path directory = Paths.get("settings/");
        ArrayList<String> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String temp = file.getFileName().toString();
                    files.add(temp.substring(0, temp.indexOf('.')));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return files;
    }
}
