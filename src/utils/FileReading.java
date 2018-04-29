package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReading {

    //Field
    private String fileName;
    private List<Double> list1 = new ArrayList<>();
    private List<Double> list2 = new ArrayList<>();
    private List<Double> list3 = new ArrayList<>();
    private List<List> collectedList = new ArrayList<>();

    //Constructor
    public FileReading(String fileName) throws IOException {

        this.fileName = fileName;

        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;

        //Load data into lists
        while ((line = reader.readLine()) != null) {
            //Split array
            String[] splitted = line.split(",");

            //Put data in each of their lists
            list1.add(Double.parseDouble(splitted[0]));
            list2.add(Double.parseDouble(splitted[1]));
            list3.add(Double.parseDouble(splitted[2]));

        }

        //List of lists
        collectedLists();

    }

    public List<List> collectedLists() {

        collectedList.add(list1);
        collectedList.add(list2);
        collectedList.add(list3);

        return collectedList;
    }

    public List<List> getCollectedList() {
        return collectedList;
    }
}