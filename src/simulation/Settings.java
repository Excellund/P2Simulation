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

    //Fishing settings
    public static float MAX_MORPHOLOGY;
    public static float MIN_MORPHOLOGY;
    public static float FISHING_QUOTAS_MIN;
    public static float FISHING_QUOTAS_MAX;
    public static float VESSEL_TRAVEL_DISTANCE;
    public static float WIDTH_STEEPNESS;
    //Spawn Vessel
    public static float MORPHOLOGY;
    public static float QUOTAS;
    //Spawn Plankton
    public static float ADD_PLANKTON;
    //Launch settings
    public static float NUM_INITIAL_SUBJECTS;
    public static float LOAD_PLANKTON;
    //Graphics settings
    public static float GAMMA;

    private static Map<String, Float> abbreviated = new HashMap<>();

    public static void defaultAbbreviated() {
        abbreviated.put("PLANKTON_GROWTH_PER_TIMESTEP", 500f);
        abbreviated.put("NUM_VESSELS", 3f);
        abbreviated.put("MAX_PLANKTON", 500000f);

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
        abbreviated.put("NUTRITION_PER_SIZE_POINT", 0.01f);
        abbreviated.put("MIN_ENERGY_MATING", 40f);
        abbreviated.put("MATING_ENERGY_CONSUMPTION", 30f);
        abbreviated.put("HEALTH_POINTS_PER_SIZE_POINTS", 1f);
        abbreviated.put("ENERGY_POINTS_PER_SIZE_POINTS", 2f);
        abbreviated.put("HEALTH_REDUCTION_ON_LOW_ENERGY", 10f);
        abbreviated.put("MIN_ENERGY_HEALTH_INCREASE", 80f);
        abbreviated.put("ENERGY_HEALTH_INCREASE", 5f);
        abbreviated.put("TIME_BEFORE_HATCH", 50f);
        abbreviated.put("CARCASS_DECAY_PER_TIMESTEP", 1f);

        abbreviated.put("MAX_MORPHOLOGY", 0.8f);
        abbreviated.put("MIN_MORPHOLOGY", 0.1f);
        abbreviated.put("FISHING_QUOTAS_MIN", 300f);
        abbreviated.put("FISHING_QUOTAS_MAX", 3000f);
        abbreviated.put("VESSEL_TRAVEL_DISTANCE", 600f);
        abbreviated.put("WIDTH_STEEPNESS", 20f);

        abbreviated.put("MORPHOLOGY", 0.5f);
        abbreviated.put("QUOTAS", 1500f);

        abbreviated.put("ADD_PLANKTON", 100000f);

        abbreviated.put("NUM_INITIAL_SUBJECTS", 300f);
        abbreviated.put("LOAD_PLANKTON", 200000f);

        abbreviated.put("GAMMA", 0.6f);


    }

    public static void useAbbreviated() {
        PLANKTON_GROWTH_PER_TIMESTEP = abbreviated.get("PLANKTON_GROWTH_PER_TIMESTEP");
        NUM_VESSELS = abbreviated.get("NUM_VESSELS");
        MAX_PLANKTON = abbreviated.get("MAX_PLANKTON");

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

        MAX_MORPHOLOGY = abbreviated.get("MAX_MORPHOLOGY");
        MIN_MORPHOLOGY = abbreviated.get("MIN_MORPHOLOGY");
        FISHING_QUOTAS_MIN = abbreviated.get("FISHING_QUOTAS_MIN");
        FISHING_QUOTAS_MAX = abbreviated.get("FISHING_QUOTAS_MAX");
        VESSEL_TRAVEL_DISTANCE = abbreviated.get("VESSEL_TRAVEL_DISTANCE");
        WIDTH_STEEPNESS = abbreviated.get("WIDTH_STEEPNESS");

        MORPHOLOGY = abbreviated.get("MORPHOLOGY");
        QUOTAS = abbreviated.get("QUOTAS");

        ADD_PLANKTON = abbreviated.get("ADD_PLANKTON");

        NUM_INITIAL_SUBJECTS = abbreviated.get("NUM_INITIAL_SUBJECTS");
        LOAD_PLANKTON = abbreviated.get("LOAD_PLANKTON");

        GAMMA = abbreviated.get("GAMMA");


    }

    public static void getAbbreviated(){
        abbreviated.put("PLANKTON_GROWTH_PER_TIMESTEP", PLANKTON_GROWTH_PER_TIMESTEP);
        abbreviated.put("NUM_VESSELS", NUM_VESSELS);
        abbreviated.put("MAX_PLANKTON", MAX_PLANKTON);

        abbreviated.put("MAX_FISH_SIZE", MAX_FISH_SIZE);
        abbreviated.put("ENERGY_PER_EGG", ENERGY_PER_EGG);
        abbreviated.put("MIN_COMPATIBILITY_MATING", MIN_COMPATIBILITY_MATING);
        abbreviated.put("MIN_PREDATION_TENDENCY", MIN_PREDATION_TENDENCY);
        abbreviated.put("MAX_ATTACK_DAMAGE", MAX_ATTACK_DAMAGE);
        abbreviated.put("ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE", ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE);
        abbreviated.put("COMPATIBILITY_STEEPNESS", COMPATIBILITY_STEEPNESS);
        abbreviated.put("COMPATIBILITY_MIDPOINT", COMPATIBILITY_MIDPOINT);
        abbreviated.put("EXPECTED_MUTATION_AMOUNT", EXPECTED_MUTATION_AMOUNT);
        abbreviated.put("MUTATION_GAUSSIAN_MEAN", MUTATION_GAUSSIAN_MEAN);
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

        abbreviated.put("MAX_MORPHOLOGY", MAX_MORPHOLOGY);
        abbreviated.put("MIN_MORPHOLOGY", MIN_MORPHOLOGY);
        abbreviated.put("FISHING_QUOTAS_MIN", FISHING_QUOTAS_MIN);
        abbreviated.put("FISHING_QUOTAS_MAX", FISHING_QUOTAS_MAX);
        abbreviated.put("VESSEL_TRAVEL_DISTANCE", VESSEL_TRAVEL_DISTANCE);
        abbreviated.put("WIDTH_STEEPNESS", WIDTH_STEEPNESS);

        abbreviated.put("MORPHOLOGY", MORPHOLOGY);
        abbreviated.put("QUOTAS", QUOTAS);

        abbreviated.put("ADD_PLANKTON", ADD_PLANKTON);

        abbreviated.put("NUM_INITIAL_SUBJECTS", NUM_INITIAL_SUBJECTS);
        abbreviated.put("LOAD_PLANKTON", LOAD_PLANKTON);

        abbreviated.put("GAMMA", GAMMA);
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

    public static ArrayList<String> getFiles(){
        Path directory = Paths.get("settings/");
        ArrayList<String> files = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)){
            for (Path file: stream){
                if (Files.isRegularFile(file)){
                    String temp = file.getFileName().toString();
                    files.add(temp.substring(0, temp.indexOf('.')));
                }
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        return files;
    }
}
