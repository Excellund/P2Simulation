package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReading {

    private List<Double> timeStep = new ArrayList<>();
    private List<Double> avgBWD = new ArrayList<>();
    private List<Double> avgMorph = new ArrayList<>();
    private List<Double> avgMaxSpawning = new ArrayList<>();
    private List<Double> avgPlankton = new ArrayList<>();
    private List<Double> avgSchoolTend = new ArrayList<>();
    private List<Double> fishCount = new ArrayList<>();
    private List<Double> carnivoreCount = new ArrayList<>();
    private List<Double> planktivoreCount = new ArrayList<>();
    private List<Double> scavengerCount = new ArrayList<>();
    private List<Double> fishEggCount = new ArrayList<>();
    private List<Double> carcassCount = new ArrayList<>();

    private List<List> collectedList = new ArrayList<>();

    //Constructor
    public FileReading(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;

        //Load data into lists
        while ((line = reader.readLine()) != null) {
            //Split array
            String[] splitted = line.split(",");

            //Put data in each of their lists
            timeStep.add(Double.parseDouble(splitted[0]));
            avgBWD.add(Double.parseDouble(splitted[1]));
            avgMorph.add(Double.parseDouble(splitted[2]));
            avgMaxSpawning.add(Double.parseDouble(splitted[3]));
            avgPlankton.add(Double.parseDouble(splitted[4]));
            avgSchoolTend.add(Double.parseDouble(splitted[5]));
            fishCount.add(Double.parseDouble(splitted[6]));
            carnivoreCount.add(Double.parseDouble(splitted[7]));
            planktivoreCount.add(Double.parseDouble(splitted[8]));
            scavengerCount.add(Double.parseDouble(splitted[9]));
            fishEggCount.add(Double.parseDouble(splitted[10]));
            carcassCount.add(Double.parseDouble(splitted[11]));
        }

        //List of lists
        collectedLists();

    }

    public List<List> collectedLists() {

        collectedList.add(timeStep);
        collectedList.add(avgBWD);
        collectedList.add(avgMorph);
        collectedList.add(avgMaxSpawning);
        collectedList.add(avgPlankton);
        collectedList.add(avgSchoolTend);
        collectedList.add(fishCount);
        collectedList.add(carnivoreCount);
        collectedList.add(planktivoreCount);
        collectedList.add(scavengerCount);
        collectedList.add(fishEggCount);
        collectedList.add(carcassCount);

        return collectedList;
    }

    public List<List> getCollectedList() {
        return collectedList;
    }
}