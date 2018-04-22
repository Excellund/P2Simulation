package simulation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Settings {
    //Simulation settings
    public static float NUM_INITIAL_SUBJECTS;
    public static float PLANKTON_GROWTH_PER_TIMESTEP;
    public static float NUM_VESSELS;

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
    public static float MUTATION_GAUSSIAN_MEAN;

    public static float SPEED_SIZE;
    public static float ENERGY_SIZE;
    public static float HEALTH_SIZE;
    //Fishing settings
    public static float MAX_MORPHOLOGY;
    public static float MIN_MORPHOLOGY;
    public static float FISHING_QUOTAS_MIN;
    public static float FISHING_QUOTAS_MAX;
    public static float VESSEL_TRAVEL_DISTANCE;
    public static float WIDTH_STEEPNESS;
    //Plankton settings
    public static float GROWTH_PER_TIME_STEP;
    public static float MAX_PLANKTON;
    //Spawn Vessel
    public static float MORPHOLOGY;
    public static float QUOTAS;
    //Spawn Plankton
    public static float ADD_PLANKTON;
    //Launch settings
    public static float LOAD_FISH;
    public static float LOAD_PLANKTON;
    //Graphics settings
    public static float GAMMA;

    private static Map<String, Float> abbreviated = new HashMap<>();

    public static void defaultAbbreviated() {
        abbreviated.put("NUM_INITIAL_SUBJECTS", 1200f);
        abbreviated.put("PLANKTON_GROWTH_PER_TIMESTEP", 300f);
        abbreviated.put("NUM_VESSELS", 3f);

        abbreviated.put("MAX_FISH_SIZE", 100f);
        abbreviated.put("ENERGY_PER_EGG", 1f);
        abbreviated.put("MIN_COMPATIBILITY_MATING", 0.8f);
        abbreviated.put("MIN_PREDATION_TENDENCY", 0.4f);
        abbreviated.put("MAX_ATTACK_DAMAGE", 1f);
        abbreviated.put("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE", 0.1f);
        abbreviated.put("COMPATIBILITY_STEEPNESS", 50f);
        abbreviated.put("COMPATIBILITY_MIDPOINT", 0.85f);
        abbreviated.put("EXPECTED_MUTATION_AMOUNT", 6f);
        abbreviated.put("MUTATION_GAUSSIAN_MEAN", 0.1f);

        abbreviated.put("SPEED_SIZE", 2f);
        abbreviated.put("ENERGY_SIZE", 400f);
        abbreviated.put("HEALTH_SIZE", 400f);

        abbreviated.put("MAX_MORPHOLOGY", 0.8f);
        abbreviated.put("MIN_MORPHOLOGY", 0.1f);
        abbreviated.put("FISHING_QUOTAS_MIN", 300f);
        abbreviated.put("FISHING_QUOTAS_MAX", 3000f);
        abbreviated.put("VESSEL_TRAVEL_DISTANCE", 600f);
        abbreviated.put("WIDTH_STEEPNESS", 20f);

        abbreviated.put("GROWTH_PER_TIME_STEP", 300f);
        abbreviated.put("MAX_PLANKTON", 1000000f);

        abbreviated.put("MORPHOLOGY", 0.5f);
        abbreviated.put("QUOTAS", 1500f);

        abbreviated.put("ADD_PLANKTON", 100000f);

        abbreviated.put("LOAD_FISH", 1200f);
        abbreviated.put("LOAD_PLANKTON", 200000f);

        abbreviated.put("GAMMA", 0.6f);


    }

    private static void useAbbreviated() {
        NUM_INITIAL_SUBJECTS = abbreviated.get("NUM_INITIAL_SUBJECTS");
        PLANKTON_GROWTH_PER_TIMESTEP = abbreviated.get("PLANKTON_GROWTH_PER_TIMESTEP");
        NUM_VESSELS = abbreviated.get("NUM_VESSELS");

        MAX_FISH_SIZE = abbreviated.get("MAX_FISH_SIZE");
        ENERGY_PER_EGG = abbreviated.get("ENERGY_PER_EGG");
        MIN_COMPATIBILITY_MATING = abbreviated.get("MIN_COMPATIBILITY_MATING");
        MIN_PREDATION_TENDENCY = abbreviated.get("MIN_PREDATION_TENDENCY");
        MAX_ATTACK_DAMAGE = abbreviated.get("MAX_ATTACK_DAMAGE");
        ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE = abbreviated.get("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE");
        COMPATIBILITY_STEEPNESS = abbreviated.get("COMPATIBILITY_STEEPNESS");
        COMPATIBILITY_MIDPOINT = abbreviated.get("COMPATIBILITY_MIDPOINT");
        EXPECTED_MUTATION_AMOUNT = abbreviated.get("EXPECTED_MUTATION_AMOUNT");
        MUTATION_GAUSSIAN_MEAN = abbreviated.get("MUTATION_GAUSSIAN_MEAN");

        SPEED_SIZE = abbreviated.get("SPEED_SIZE");
        ENERGY_SIZE = abbreviated.get("ENERGY_SIZE");
        HEALTH_SIZE = abbreviated.get("HEALTH_SIZE");

        MAX_MORPHOLOGY = abbreviated.get("MAX_MORPHOLOGY");
        MIN_MORPHOLOGY = abbreviated.get("MIN_MORPHOLOGY");
        FISHING_QUOTAS_MIN = abbreviated.get("FISHING_QUOTAS_MIN");
        FISHING_QUOTAS_MAX = abbreviated.get("FISHING_QUOTAS_MAX");
        VESSEL_TRAVEL_DISTANCE = abbreviated.get("VESSEL_TRAVEL_DISTANCE");
        WIDTH_STEEPNESS = abbreviated.get("WIDTH_STEEPNESS");

        GROWTH_PER_TIME_STEP = abbreviated.get("GROWTH_PER_TIME_STEP");
        MAX_PLANKTON = abbreviated.get("MAX_PLANKTON");

        MORPHOLOGY = abbreviated.get("MORPHOLOGY");
        QUOTAS = abbreviated.get("QUOTAS");

        ADD_PLANKTON = abbreviated.get("ADD_PLANKTON");

        LOAD_FISH = abbreviated.get("LOAD_FISH");
        LOAD_PLANKTON = abbreviated.get("LOAD_PLANKTON");

        GAMMA = abbreviated.get("GAMMA");


    }

    public static void toFile(String name) {
        Path path = Paths.get("settings/");

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
}
