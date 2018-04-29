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

    public ObservableList<Series<Double, Double>> getChartData(int xChoice, int yChoice) {

        ObservableList<Series<Double, Double>> result = FXCollections.observableArrayList();
        Series<Double, Double> dataSet = new Series<>();

        //Calls the file
        FileReading file = null;
        try {
            file = new FileReading("TestData");
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
    int tickUnit(double listMax) {

        int tickUnitValue = 1;

        if (listMax >= 20) {

            tickUnitValue = 5;
        } else if (listMax >= 50) {

            tickUnitValue = 10;

        } else if (listMax >= 1000) {

            tickUnitValue = 500;
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