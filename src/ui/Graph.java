package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import utils.FileReading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Graph {

    private List<Double> xCoordinate = new ArrayList<>();
    private List<Double> yCoordinate = new ArrayList<>();
    private int count = 163;

    public void setCount(int count) {
        this.count = count;
    }

    public ObservableList<Series<Double, Double>> getChartData(int xChoice, int yChoice) {

        ObservableList<Series<Double, Double>> result = FXCollections.observableArrayList();
        Series<Double, Double> dataSet = new Series<>();

        //Calls the file
        FileReading file = null;
        try {
            file = new FileReading("output/data_" + count + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < (double) file.getCollectedList().get(xChoice).size(); i++) {

            xCoordinate.add((double) file.getCollectedList().get(xChoice).get(i));
            yCoordinate.add((double) file.getCollectedList().get(yChoice).get(i));

        }

        double xValue;
        double yValue;

        //Store the data into dataset
        for (int i = 0; i < xCoordinate.size(); i++) {

            xValue = xCoordinate.get(i);
            yValue = yCoordinate.get(i);

            dataSet.getData().add(new XYChart.Data(xValue, yValue));

        }

        result.add(dataSet);

        return result;
    }

    //Tick units so the graph doesn't get too cluttered
    double tickUnit(double listMax) {

        double tickUnitValue = 1;

        if(listMax >= 100000){

            tickUnitValue = 10000;

        } else if(listMax >= 20000){

            tickUnitValue = 5000;

        } else if (listMax >= 5000){

            tickUnitValue = 1000;

        } else if (listMax >= 1000) {

            tickUnitValue = 500;

        } else if(listMax >= 500){

            tickUnitValue = 100;

        } else if(listMax >= 100) {

            tickUnitValue = 100;

        } else if (listMax >= 50) {

            tickUnitValue = 10;

        } else if (listMax >= 20) {

            tickUnitValue = 5;

        } else if (listMax < 20){

            tickUnitValue = 1;

        } else if (listMax <= 1){

            tickUnitValue = 0.1;
        }

        return tickUnitValue;
    }

    public List<Double> getxCoordinate() {
        return xCoordinate;
    }

    public List<Double> getyCoordinate() {
        return yCoordinate;
    }
}

//http://www.java2s.com/Tutorials/Java/JavaFX/0860__JavaFX_ScatterChart.htm
//https://docs.oracle.com/javafx/2/charts/css-styles.htm