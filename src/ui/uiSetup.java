package ui;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import simulation.Engine;
import simulation.Settings;
import simulation.Simulation;
import simulation.Snapshot;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class uiSetup {

    public static Scene getScene(Stage primaryStage){
        DragListener dragListener = new DragListener();
        final Delta deltaDrag = new Delta();
        VBox root = new VBox();
        Scene scene = new Scene(root, 1500, 1080);

        Toolbar toolbar = new Toolbar("EFSOS", 1500, "toolbar");

        ContentBoxFactory factory = new ContentBoxFactory(dragListener);
        HBox rowContainer = new HBox();
        Pane canvasContainer = new Pane();
        Canvas canvas = new Canvas(750, 750);
        ContentArea areaOne = new ContentArea(375, 750, dragListener);
        ContentArea areaTwo = new ContentArea(375, 750, dragListener);
        ContentArea graphAreaOne = new ContentArea(375, 280, dragListener);
        ContentArea graphAreaTwo = new ContentArea(750, 280, dragListener);
        ContentArea graphAreaThree = new ContentArea(375, 280, dragListener);

        ContentBox interactionBox = factory.generateInteractionBox(375);
        ContentBox spawnBox = factory.generateSpawnBox(375);
        ContentBox launchBox = factory.generateLaunchBox(375);
        ContentBox navigator = factory.generateNavigator(375, interactionBox, spawnBox, launchBox, areaTwo);

        Simulation simulation = new Simulation(750, 750);
        Engine engine = new Engine(simulation, canvas);

        canvasContainer.setStyle("-fx-background-color:black");
        canvasContainer.getChildren().add(canvas);

        setOnMouseEvents(deltaDrag, primaryStage, toolbar, engine);

        Thread engineThread = new Thread(engine, "Engine");

        engineThread.start();

        rowContainer.getChildren().addAll(areaOne, canvasContainer, areaTwo);

        MenuBar menuBar = getMenuBar(primaryStage, navigator, areaOne, simulation, graphAreaOne, graphAreaTwo,
                graphAreaThree, factory, interactionBox, root, toolbar, rowContainer);

        //GUI style
        root.getStyleClass().add("box");

        return scene;
    }

    private static MenuBar getMenuBar(Stage primaryStage, ContentBox navigator, ContentArea areaOne, Simulation simulation
            ,ContentArea graphAreaOne, ContentArea graphAreaTwo, ContentArea graphAreaThree, ContentBoxFactory factory
            ,ContentBox interactionBox, VBox root, Toolbar toolbar, HBox rowContainer){

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        Menu menuTools = new Menu("Tools");

        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuTools);

        MenuItem itemNavigator = new MenuItem("Navigator");

        menuView.getItems().add(itemNavigator);

        itemNavigator.setOnAction(event ->
        {
            if (navigator.getParent() == null) {
                areaOne.getChildren().add(0, navigator);
            }

            event.consume();
        });
        areaOne.getChildren().add(navigator);

        setOnFileAction(menuFile, primaryStage, menuEdit, simulation);
        HBox graphContainer = graphEvents(graphAreaOne, graphAreaTwo, graphAreaThree, menuView, factory, interactionBox);
        root.getChildren().addAll(toolbar, menuBar, rowContainer, graphContainer);

        return menuBar;
    }

    private static void setOnMouseEvents(Delta deltaDrag, Stage primaryStage, Toolbar toolbar, Engine engine){
        Button exitButton = new Button("x");
        Button minimizeButton = new Button("-");
        toolbar.setOnMousePressed(event ->
        {
            //calculate mouse position relative to window size on mouse press
            deltaDrag.x = primaryStage.getX() - event.getScreenX();
            deltaDrag.y = primaryStage.getY() - event.getScreenY();
        });

        toolbar.setOnMouseDragged(event ->
        {
            //move window to mouse position using calculated mouse position relative to window size
            primaryStage.setX(event.getScreenX() + deltaDrag.x);
            primaryStage.setY(event.getScreenY() + deltaDrag.y);
        });

        minimizeButton.setOnMouseReleased(event -> primaryStage.setIconified(true));
        toolbar.addButton(minimizeButton);
        toolbar.addButton(exitButton);

        //custom exit and minimize buttons
        exitButton.setOnMouseReleased(event ->
        {
            engine.stop();
            primaryStage.close();
        });
    }

    private static HBox graphEvents(ContentArea graphAreaOne, ContentArea graphAreaTwo, ContentArea graphAreaThree, Menu menuView, ContentBoxFactory factory, ContentBox interactionBox){
        HBox graphContainer = new HBox();

        graphContainer.getChildren().addAll(graphAreaOne, graphAreaTwo, graphAreaThree);

        MenuItem itemGraph1 = new MenuItem("Graph 1");
        MenuItem itemGraph2 = new MenuItem("Graph 2");
        MenuItem itemGraph3 = new MenuItem("Graph 3");

        menuView.getItems().addAll(itemGraph1, itemGraph2, itemGraph3);

        ContentBox graph1 = factory.interactionBoxSetContentGraph(interactionBox)[0];
        ContentBox graph2 = factory.interactionBoxSetContentGraph(interactionBox)[1];
        ContentBox graph3 = factory.interactionBoxSetContentGraph(interactionBox)[2];

        //If graph1 isn't in graphAreaOne, it will create the graph there, when button is clicked
        itemGraph1.setOnAction(event ->
        {
            if (graph1.getParent() == null) {
                graphAreaOne.getChildren().add(0, graph1);
            }

            event.consume();
        });

        //If graph2 isn't in graphAreaTwo, it will create the graph there, when button is clicked
        itemGraph2.setOnAction(event ->
        {
            if (graph2.getParent() == null) {
                graphAreaTwo.getChildren().add(0, graph2);
            }

            event.consume();
        });

        //If graph3 isn't in graphAreaThree, it will create the graph there, when button is clicked
        itemGraph3.setOnAction(event ->
        {
            if (graph3.getParent() == null) {
                graphAreaThree.getChildren().add(0, graph3);
            }

            event.consume();
        });
        return graphContainer;
    }

    private static void setOnFileAction(Menu menuFile, Stage primaryStage, Menu menuEdit, Simulation simulation){
        MenuItem itemSaveSettings = new MenuItem("Save Settings");
        MenuItem itemLoadSettings = new MenuItem("Load Settings");
        MenuItem itemSaveSnapshot = new MenuItem("Save Snapshot");
        MenuItem itemLoadSnapshot = new MenuItem("Load Snapshot");

        menuFile.getItems().addAll(itemSaveSettings, itemLoadSettings, itemSaveSnapshot, itemLoadSnapshot);

        saveSettings(itemSaveSettings);
        loadSettings(itemLoadSettings);
        saveSnapshot(itemSaveSnapshot, primaryStage, simulation);
        loadSnapshot(itemLoadSnapshot, primaryStage);

        MenuItem itemRestartSimulation = new MenuItem("Restart simulation");
        MenuItem itemPauseSimulation = new MenuItem("Start/Pause");

        menuEdit.getItems().addAll(itemPauseSimulation, itemRestartSimulation);

        itemRestartSimulation.setOnAction(event -> {
            Simulation restartSimulation = new Simulation(750, 750);
        });
    }

    private static void saveSettings(MenuItem itemSaveSettings){
        itemSaveSettings.setOnAction(event -> {
            TextInputDialog saveSettings = new TextInputDialog();
            saveSettings.setTitle("Save Settings");
            saveSettings.setContentText("Save as");
            Optional<String> result = saveSettings.showAndWait();
            result.ifPresent(file -> {
                Settings.toFile(file);
            });
        });
    }

    private static void loadSettings(MenuItem itemLoadSettings){
        itemLoadSettings.setOnAction(event ->  {
            List<String> files = Settings.getFiles();
            if (files.size() == 0){
                Settings.defaultAbbreviated();
                Settings.useAbbreviated();
                Settings.toFile("default");
                files.add("default");
            }

            ChoiceDialog<String> loadSettings = new ChoiceDialog<>(files.get(0), files);
            loadSettings.setTitle("Load settings");
            loadSettings.setContentText("Choose Settings:");
            Optional<String> result = loadSettings.showAndWait();
            result.ifPresent(file -> Settings.fromFile(file));
        });
    }

    private static void loadSnapshot(MenuItem itemLoadSnapshot, Stage primaryStage){
        itemLoadSnapshot.setOnAction(event -> {
            FileChooser fileChooserOpen = new FileChooser();
            fileChooserOpen.setTitle("Open snapshot");
            FileChooser.ExtensionFilter snapshotFilter = new FileChooser.ExtensionFilter("Snapshot file", "*.snapshot");
            fileChooserOpen.getExtensionFilters().add(snapshotFilter);
            File file = fileChooserOpen.showOpenDialog(primaryStage);
            if (file != null){
                Snapshot.loadSnapshot(file.getAbsolutePath());
            }
        });
    }

    private static void saveSnapshot(MenuItem itemSaveSnapshot, Stage primaryStage, Simulation simulation){
        itemSaveSnapshot.setOnAction(event ->  {
            FileChooser fileChooserSave = new FileChooser();
            fileChooserSave.setTitle("Save snapshot");
            fileChooserSave.getExtensionFilters().add(new FileChooser.ExtensionFilter("snapshot", "*.snapshot"));
            fileChooserSave.setInitialFileName("*.snapshot");
            File file = fileChooserSave.showSaveDialog(primaryStage);
            if (file != null){
                Snapshot snapshot = new Snapshot(simulation);
                Snapshot.saveSnapshot(file.getAbsolutePath(), snapshot);
            }
        });
    }

}
class Delta {
    //used to store mouse position relative to window size
    //The double variables must be encapsulated to allow change in lambda expressions
    double x, y;
}
