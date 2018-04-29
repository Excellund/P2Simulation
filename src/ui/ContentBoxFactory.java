package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import simulation.Settings;

import java.util.ArrayList;


public class ContentBoxFactory {
    private DragListener dragListener;

    private StackPane root1 = new StackPane();
    private StackPane root2 = new StackPane();
    private StackPane root3 = new StackPane();

    public ContentBoxFactory(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public ContentBox generateNavigator(double width, ContentBox interactionBox, ContentBox spawnBox, ContentBox launchBox, ContentArea interactionBoxArea2) {
        ContentBox contentBox = new ContentBox("Navigator", width, dragListener);
        TreeItem<String> menuRoot = new TreeItem<>("simulation name");
        TreeView<String> menu = new TreeView<>(menuRoot);

        TreeItem<String> itemSettings = new TreeItem<>("Settings");
        TreeItem<String> itemSimulation = new TreeItem<>("Simulation");
        TreeItem<String> itemFish = new TreeItem<>("Fish");
        TreeItem<String> itemFishery = new TreeItem<>("Fishery");
        TreeItem<String> itemGraph = new TreeItem<>("Graph");
        TreeItem<String> itemGraphics = new TreeItem<>("Graphics");
        TreeItem<String> itemSaveSettings = new TreeItem<>("Save settings");


        itemSettings.getChildren().addAll(itemSimulation, itemFish, itemFishery, itemGraph, itemGraphics, itemSaveSettings);

        TreeItem<String> itemSnapshot = new TreeItem<>("Snapshot");

        TreeItem<String> itemAddItems = new TreeItem<>("Add items");
        TreeItem<String> itemAddVessel = new TreeItem<>("Vessel");
        TreeItem<String> itemAddPlankton = new TreeItem<>("Plankton");

        TreeItem<String> itemLaunch = new TreeItem<>("Launch simulation");
        TreeItem<String> itemStartLaunch = new TreeItem<>("New launch");
        TreeItem<String> itemStartSnapshot = new TreeItem<>("Launch snapshot");


        itemAddItems.getChildren().addAll(itemAddVessel, itemAddPlankton);
        itemLaunch.getChildren().addAll(itemStartLaunch, itemStartSnapshot);
        menuRoot.getChildren().addAll(itemSettings, itemSnapshot, itemAddItems, itemLaunch);

        contentBox.setContent(menu);


        menu.getSelectionModel().selectedItemProperty().addListener(event ->
        {
            switch (menu.getSelectionModel().getSelectedItem().getParent().getValue()) {
                case "Settings":
                    switch (menu.getSelectionModel().selectedItemProperty().getValue().getValue()) {
                        case "Simulation":
                            interactionBoxSetContextSimulation(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);
                            break;

                        case "Fish":
                            interactionBoxSetContextFish(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);
                            break;

                        case "Fishery":
                            interactionBoxSetContextFishery(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);

                            break;

                        case "Graph":
                            interactionBoxSetContentGraph(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);
                            break;

                        case "Graphics":
                            interactionBoxSetContextGraphics(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);
                            break;

                        case "Save settings":
                            interactionBoxSetContentSaveSettings(interactionBox);
                            interactionBoxChecker(interactionBox, interactionBoxArea2, menu);
                            break;
                    }
                    break;

                case "Snapshot":
                    System.out.println("hej");
                    break;

                case "Add items":
                    switch (menu.getSelectionModel().selectedItemProperty().getValue().getValue()) {
                        case "Vessel":
                            spawnBoxSetContextVessel(spawnBox);
                            spawnBoxChecker(spawnBox, interactionBoxArea2, menu);
                            break;

                        case "Plankton":
                            spawnBoxSetContextPlankton(spawnBox);
                            spawnBoxChecker(spawnBox, interactionBoxArea2, menu);
                            break;
                    }
                    break;

                case "Launch simulation":
                    switch (menu.getSelectionModel().selectedItemProperty().getValue().getValue()) {
                        case "New launch":
                            launchBoxSetContextStart(launchBox);
                            launchBoxChecker(launchBox, interactionBoxArea2, menu);
                            break;

                        case "Launch snapshot":
                            launchBoxSetContextSnapshot(launchBox);
                            launchBoxChecker(launchBox, interactionBoxArea2, menu);
                            break;
                    }
                    break;
            }


        });


        return contentBox;
    }

    public void interactionBoxChecker(ContentBox interactionBox, ContentArea interactionBoxArea2, TreeView<String> menu) {
        if (interactionBox.getParent() == null && menu.getSelectionModel().selectedItemProperty().getValue().isLeaf()) {
            interactionBoxArea2.getChildren().add(interactionBox);
        }
    }

    public void spawnBoxChecker(ContentBox spawnBox, ContentArea interactionBoxArea2, TreeView<String> menu) {
        if (spawnBox.getParent() == null && menu.getSelectionModel().selectedItemProperty().getValue().isLeaf()) {
            interactionBoxArea2.getChildren().add(spawnBox);
        }
    }

    public void launchBoxChecker(ContentBox launchBox, ContentArea interactionBoxArea2, TreeView<String> menu) {
        if (launchBox.getParent() == null && menu.getSelectionModel().selectedItemProperty().getValue().isLeaf()) {
            interactionBoxArea2.getChildren().add(launchBox);
        }
    }

    public ContentBox generateInteractionBox(double width) {
        ContentBox interactionBox = new ContentBox("Interaction", width, dragListener);


        return interactionBox;
    }

    public ContentBox generateSpawnBox(double width) {
        ContentBox spawnBox = new ContentBox("Spawn items", width, dragListener);

        return spawnBox;
    }

    public ContentBox generateLaunchBox(double width) {
        ContentBox launchBox = new ContentBox("Launch", width, dragListener);

        return launchBox;
    }

    public void interactionBoxSetContextSimulation(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Simulation settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelNumVessel = new Label("Number of vessels");
        TextField textNumVessel = new TextField(Float.toString(Settings.NUM_VESSELS));
        Label labelPlanktonGrowth = new Label("Growth pr. time step");
        TextField textPlanktonGrowth = new TextField(Float.toString(Settings.PLANKTON_GROWTH_PER_TIMESTEP));
        Label labelPlanktonMax = new Label("Max plankton");
        TextField textPlanktonMax = new TextField(Float.toString(Settings.MAX_PLANKTON));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.NUM_VESSELS = Float.parseFloat(textNumVessel.getText());
                Settings.PLANKTON_GROWTH_PER_TIMESTEP = Float.parseFloat(textPlanktonGrowth.getText());
                Settings.MAX_PLANKTON = Float.parseFloat(textPlanktonMax.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelNumVessel, labelPlanktonGrowth, labelPlanktonMax);
        columnB.getChildren().addAll(textNumVessel, textPlanktonGrowth, textPlanktonMax);

        columnB.getChildren().add(saveButton);
        interactionBox.setContent(mainContent);
    }


    public void interactionBoxSetContextFish(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Fish settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);


        mainContent.getChildren().addAll(columnA, columnB);
        Label labelEnergyEgg = new Label("Energy per egg");
        TextField textEnergyEgg = new TextField(Float.toString(Settings.ENERGY_PER_EGG));
        Label labelEnergy = new Label("Energy/Size");
        TextField textEnergy = new TextField(Float.toString(Settings.ENERGY_POINTS_PER_SIZE_POINTS));
        Label labelHealth = new Label("Health/Size");
        TextField textHealth = new TextField(Float.toString(Settings.HEALTH_POINTS_PER_SIZE_POINTS));
        Label labelMaxSize = new Label("Max Fish size");
        TextField textMaxSize = new TextField(Float.toString(Settings.MAX_FISH_SIZE));
        Label labelMinMating = new Label("Compatibility for mating");
        TextField textMinMating = new TextField(Float.toString(Settings.MIN_COMPATIBILITY_MATING));
        Label labelMinPredation = new Label("Predation Tendency");
        TextField textMinPredation = new TextField(Float.toString(Settings.MIN_PREDATION_TENDENCY));
        Label labelMaxDamage = new Label("Max Damage");
        TextField textMaxDamage = new TextField(Float.toString(Settings.MAX_ATTACK_DAMAGE));
        Label labelEnergyUseAttack = new Label("Energy used for attack");
        TextField textEnergyUseAttack = new TextField(Float.toString(Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE));
        Label labelCompatibilitySteepness = new Label("Compatibility for steepness");
        TextField textCompatibilitySteepness = new TextField(Float.toString(Settings.COMPATIBILITY_STEEPNESS));
        Label labelCompatibilityMidpoint = new Label("Compatibility for midpoint");
        TextField textCompatibilityMidpoint = new TextField(Float.toString(Settings.COMPATIBILITY_MIDPOINT));
        Label labelMutationAmount = new Label("Mutation amount");
        TextField textMutationAmount = new TextField(Float.toString(Settings.EXPECTED_MUTATION_AMOUNT));
        Label labelMutationGaussian = new Label("Mutation gaussian");
        TextField textMutationGaussian = new TextField(Float.toString(Settings.MUTATION_GAUSSIAN_MEAN));
        Label labelNutritionSize = new Label("Nutrition per size");
        TextField textNutritionSize = new TextField(Float.toString(Settings.NUTRITION_PER_SIZE_POINT));
        Label labelMinEnergyMating = new Label("Min energy for mating");
        TextField textMinEnergyMating = new TextField(Float.toString(Settings.MIN_ENERGY_MATING));
        Label labelMatingEnergyUse = new Label("Energi used for mating");
        TextField textMatingEnergyUse = new TextField(Float.toString(Settings.MATING_ENERGY_CONSUMPTION));
        Label labelHealthReducLowEnergy = new Label("Health reduction with low energy");
        TextField textHealthReducLowEnergy = new TextField(Float.toString(Settings.HEALTH_REDUCTION_ON_LOW_ENERGY));
        Label labelMinEnergyIncrease = new Label("Min energy for increase health");
        TextField textMinEenergyIncrease = new TextField(Float.toString(Settings.MIN_ENERGY_HEALTH_INCREASE));
        Label labelEnergyHealthIncrease = new Label("Energy per time step");
        TextField textEnergyHealthIncrease = new TextField(Float.toString(Settings.ENERGY_HEALTH_INCREASE));
        Label labelTimeToHatch = new Label("Hatch time");
        TextField textTimeToHatch = new TextField(Float.toString(Settings.TIME_BEFORE_HATCH));
        Label labelCarcassDecay = new Label("Carcass decay time");
        TextField textCarcassDecay = new TextField(Float.toString(Settings.CARCASS_DECAY_PER_TIMESTEP));


        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.ENERGY_PER_EGG = Float.parseFloat(textEnergyEgg.getText());
                Settings.ENERGY_POINTS_PER_SIZE_POINTS = Float.parseFloat(textEnergy.getText());
                Settings.HEALTH_POINTS_PER_SIZE_POINTS = Float.parseFloat(textHealth.getText());
                Settings.MAX_FISH_SIZE = Float.parseFloat(textMaxSize.getText());
                Settings.MIN_COMPATIBILITY_MATING = Float.parseFloat(textMinMating.getText());
                Settings.MIN_PREDATION_TENDENCY = Float.parseFloat(textMinPredation.getText());
                Settings.MAX_ATTACK_DAMAGE = Float.parseFloat(textMaxDamage.getText());
                Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE = Float.parseFloat(textEnergyUseAttack.getText());
                Settings.COMPATIBILITY_STEEPNESS = Float.parseFloat(textCompatibilitySteepness.getText());
                Settings.COMPATIBILITY_MIDPOINT = Float.parseFloat(textCompatibilityMidpoint.getText());
                Settings.EXPECTED_MUTATION_AMOUNT = Float.parseFloat(textMutationAmount.getText());
                Settings.MUTATION_GAUSSIAN_MEAN = Float.parseFloat(textMutationGaussian.getText());
                Settings.NUTRITION_PER_SIZE_POINT = Float.parseFloat(textNutritionSize.getText());
                Settings.MIN_ENERGY_MATING = Float.parseFloat(textMinEnergyMating.getText());
                Settings.MATING_ENERGY_CONSUMPTION = Float.parseFloat(textMatingEnergyUse.getText());
                Settings.HEALTH_REDUCTION_ON_LOW_ENERGY = Float.parseFloat(textHealthReducLowEnergy.getText());
                Settings.MIN_ENERGY_HEALTH_INCREASE = Float.parseFloat(textMinEenergyIncrease.getText());
                Settings.ENERGY_HEALTH_INCREASE = Float.parseFloat(textEnergyHealthIncrease.getText());
                Settings.TIME_BEFORE_HATCH = Float.parseFloat(textTimeToHatch.getText());
                Settings.CARCASS_DECAY_PER_TIMESTEP = Float.parseFloat(textCarcassDecay.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelEnergyEgg, labelHealth, labelMinMating, labelMaxDamage, labelCompatibilitySteepness, labelMutationAmount, labelNutritionSize, labelMatingEnergyUse, labelMinEnergyIncrease, labelTimeToHatch);
        columnB.getChildren().addAll(textEnergyEgg, textHealth, textMinMating, textMaxDamage, textCompatibilitySteepness, textMutationAmount, textNutritionSize, textMatingEnergyUse, textMinEenergyIncrease, textTimeToHatch);
        columnA.getChildren().addAll(labelEnergy, labelMaxSize, labelMinPredation, labelEnergyUseAttack, labelCompatibilityMidpoint, labelMutationGaussian, labelMinEnergyMating, labelHealthReducLowEnergy, labelEnergyHealthIncrease, labelCarcassDecay);
        columnB.getChildren().addAll(textEnergy, textMaxSize, textMinPredation, textEnergyUseAttack, textCompatibilityMidpoint, textMutationGaussian, textMinEnergyMating, textHealthReducLowEnergy, textEnergyHealthIncrease, textCarcassDecay);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void interactionBoxSetContextFishery(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Fishery settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelTravel = new Label("Vessels traveldistance");
        TextField textTravel = new TextField(Float.toString(Settings.VESSEL_TRAVEL_DISTANCE));
        Label labelMorphologyMin = new Label("Morphology Min");
        TextField textMorphologyMin = new TextField(Float.toString(Settings.MIN_MORPHOLOGY));
        Label labelMorphologyMax = new Label("Max");
        TextField textMorphologyMax = new TextField(Float.toString(Settings.MAX_MORPHOLOGY));
        Label labelQuotasMin = new Label("Fishing quotas Min");
        TextField textQuotasMin = new TextField(Float.toString(Settings.FISHING_QUOTAS_MIN));
        Label labelQuotasMax = new Label("Max");
        TextField textQuotasMax = new TextField(Float.toString(Settings.FISHING_QUOTAS_MAX));
        Label labelWidthSteepness = new Label("Steepness width");
        TextField textWidthSteepness = new TextField(Float.toString(Settings.WIDTH_STEEPNESS));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.VESSEL_TRAVEL_DISTANCE = Float.parseFloat(textTravel.getText());
                Settings.MIN_MORPHOLOGY = Float.parseFloat(textMorphologyMin.getText());
                Settings.MAX_MORPHOLOGY = Float.parseFloat(textMorphologyMax.getText());
                Settings.FISHING_QUOTAS_MIN = Float.parseFloat(textMorphologyMin.getText());
                Settings.FISHING_QUOTAS_MAX = Float.parseFloat(textMorphologyMax.getText());
                Settings.WIDTH_STEEPNESS = Float.parseFloat(textWidthSteepness.getText());

            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });


        columnA.getChildren().addAll(labelMorphologyMin, labelQuotasMin, labelTravel, labelMorphologyMax, labelQuotasMax, labelWidthSteepness);
        columnB.getChildren().addAll(textMorphologyMin, textQuotasMin, textTravel, textMorphologyMax, textQuotasMax, textWidthSteepness);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void interactionBoxSetContextGraphics(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Graphics settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelGamma = new Label("Gamma");
        TextField textGamma = new TextField(Float.toString(Settings.GAMMA));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.GAMMA = Float.parseFloat(textGamma.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().add(labelGamma);
        columnB.getChildren().add(textGamma);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void interactionBoxSetContentSaveSettings(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Save settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelSave = new Label("Save as");
        TextField textSave = new TextField();

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            Settings.toFile(textSave.getText());
        });

        columnA.getChildren().addAll(labelSave);
        columnB.getChildren().addAll(textSave, saveButton);

        interactionBox.setContent(mainContent);
    }

    public void spawnBoxSetContextVessel(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Spawn Vessel");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelMorphology = new Label("Morphology");
        TextField textMorphology = new TextField(Float.toString(Settings.MORPHOLOGY));
        Label labelQuotas = new Label("Quotas");
        TextField textQuotas = new TextField(Float.toString(Settings.QUOTAS));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.MORPHOLOGY = Float.parseFloat(textMorphology.getText());
                Settings.QUOTAS = Float.parseFloat(textQuotas.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });


        columnB.getChildren().addAll(textMorphology, textQuotas);
        columnA.getChildren().addAll(labelMorphology, labelQuotas);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void spawnBoxSetContextPlankton(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Spawn plankton");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelAddPlankton = new Label("Increase density");
        TextField textAddPlankton = new TextField(Float.toString(Settings.ADD_PLANKTON));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.ADD_PLANKTON = Float.parseFloat(textAddPlankton.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelAddPlankton);
        columnB.getChildren().addAll(textAddPlankton);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void launchBoxSetContextStart(ContentBox launchBox) {
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);
        VBox columnC = new VBox();
        VBox columnD = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB, columnC, columnD);

        Label labelFishLoad = new Label("Fish load");
        TextField textFishLoad = new TextField(Float.toString(Settings.NUM_INITIAL_SUBJECTS));
        Label labelPlanktonLoad = new Label("Plankton load");
        TextField textPlanktonLoad = new TextField(Float.toString(Settings.LOAD_PLANKTON));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.NUM_INITIAL_SUBJECTS = Float.parseFloat(textFishLoad.getText());
                Settings.LOAD_PLANKTON = Float.parseFloat(textPlanktonLoad.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelFishLoad, labelPlanktonLoad);
        columnB.getChildren().addAll(textFishLoad, textPlanktonLoad);

        ArrayList<String> files = Settings.getFiles();

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(files);

        Button launchSettings = new Button("Upload Settings");
        launchSettings.getStyleClass().add("buttonContent");
        launchSettings.setOnAction(event ->
        {
            Settings.fromFile(choiceBox.getValue());
        });

        columnC.getChildren().add(choiceBox);
        columnD.getChildren().add(launchSettings);
        launchBox.setContent(mainContent);
    }

    public void launchBoxSetContextSnapshot(ContentBox launchBox) {
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);
        VBox columnC = new VBox();
        VBox columnD = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB, columnC, columnD);

        //Snapshot hello = new Snapshot();
        //Snapshot.saveSnapshot("snapshots/hello", hello);

        launchBox.setContent(mainContent);
    }

    public ContentBox[] interactionBoxSetContentGraph(ContentBox interactionBox) {

        HBox mainContent = new HBox(10);
        GridPane grid = new GridPane();

        //ChoiceBox choices
        ObservableList<String> options = FXCollections.observableArrayList("List 1", "List 2", "List 3");

        //XY-coordinates for the different graphs
        int[] XYGraph1 = new int[2];
        int[] XYGraph2 = new int[2];
        int[] XYGraph3 = new int[2];

        //Array of content boxes which holds the three graphs
        ContentBox[] graphs = new ContentBox[3];

        //Graph 1
        //Labels
        Label graph1 = new Label("Graph 1");
        Label xData1 = new Label("x plot: ");
        Label yData1 = new Label("y plot:");

        //Choice boxes
        final ChoiceBox xChoiceBox1 = new ChoiceBox(options);
        final ChoiceBox yChoiceBox1 = new ChoiceBox(options);

        //Save button to make new graph
        Button saveButton1 = new Button("Save");
        saveButton1.getStyleClass().add("buttonContent");

        grid.add(graph1, 0, 0);
        grid.add(xData1, 0, 1);
        grid.add(yData1, 2, 1);
        grid.add(xChoiceBox1, 1, 1);
        grid.add(yChoiceBox1, 3, 1);
        grid.add(saveButton1, 4, 1);

        //Chosen value in choice box gets stored in XY-coordinates
        xChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph1[0] = getChoice(xChoiceBox1));
        yChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph1[1] = getChoice(yChoiceBox1));

        //First graph is generated and stored in the array of graphs
        ContentBox graphOneGenerate = generateGraph(root1, XYGraph1[0], XYGraph1[1]);
        graphs[0] = graphOneGenerate;

        //Update action when save button is pressed
        saveButton1.setOnAction(e -> updateGraph(root1, XYGraph1[0], XYGraph1[1]));

        //Graph 2
        //Labels
        Label graph2 = new Label("Graph 2");
        Label xData2 = new Label("x plot: ");
        Label yData2 = new Label("y plot:");

        // Choice boxes
        final ChoiceBox xChoiceBox2 = new ChoiceBox(options);
        final ChoiceBox yChoiceBox2 = new ChoiceBox(options);

        //Save button for graph 2
        Button saveButton2 = new Button("Save");
        saveButton2.getStyleClass().add("buttonContent");

        grid.add(graph2, 0, 2);
        grid.add(xData2, 0, 3);
        grid.add(yData2, 2, 3);
        grid.add(xChoiceBox2, 1, 3);
        grid.add(yChoiceBox2, 3, 3);
        grid.add(saveButton2, 4, 3);

        //Chosen value in choice box gets stored in XY-coordinates
        xChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph2[0] = getChoice(xChoiceBox2));
        yChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph2[1] = getChoice(yChoiceBox2));

        //Second graph is generated and stored in the array of graphs
        ContentBox graphTwoGenerate = generateGraph(root2, XYGraph2[0], XYGraph2[1]);
        graphs[1] = graphTwoGenerate;

        //Update action when save button is pressed
        saveButton2.setOnAction(e -> updateGraph(root2, XYGraph2[0], XYGraph2[1]));

        //Graph 3
        //Labels
        Label graph3 = new Label("Graph 3");
        Label xData3 = new Label("x plot: ");
        Label yData3 = new Label("y plot:");

        //Choice boxes
        final ChoiceBox xChoiceBox3 = new ChoiceBox(options);
        final ChoiceBox yChoiceBox3 = new ChoiceBox(options);

        //Save button
        Button saveButton3 = new Button("Save");
        saveButton3.getStyleClass().add("buttonContent");

        grid.add(graph3, 0, 4);
        grid.add(xData3, 0, 5);
        grid.add(yData3, 2, 5);
        grid.add(xChoiceBox3, 1, 5);
        grid.add(yChoiceBox3, 3, 5);
        grid.add(saveButton3, 4, 5);

        //Chosen value in choice box gets stored in XY-coordinates
        xChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph3[0] = getChoice(xChoiceBox3));
        yChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> XYGraph3[1] = getChoice(yChoiceBox3));

        //Third graph is generated and stored in the array of graphs
        ContentBox graphThreeGenerate = generateGraph(root3, XYGraph3[0], XYGraph3[1]);
        graphs[2] = graphThreeGenerate;

        //Update action when save button is pressed
        saveButton3.setOnAction(e -> updateGraph(root3, XYGraph3[0], XYGraph3[1]));

        //Add all graph UI to main content
        mainContent.getChildren().add(grid);

        //Set main content as content of interaction box
        interactionBox.setContent(mainContent);

        return graphs;
    }

    //Gives the string values on the list an int value to read the correct list
    public int getChoice(ChoiceBox<String> choiceBox) {

        String choice = choiceBox.getValue();
        int choiceValue = 0;

        if (choice.equals("List 1")) {
            choiceValue = 0;
        } else if (choice.equals("List 2")) {
            choiceValue = 1;
        } else if (choice.equals("List 3")) {
            choiceValue = 2;
        }

        return choiceValue;
    }

    public ContentBox generateGraph(StackPane root, int xChoice, int yChoice) {

        Graph graph = new Graph();
        ObservableList<XYChart.Series<Double, Double>> dataSet = graph.getChartData(xChoice, yChoice);

        //Name of content box
        ContentBox contentBox = new ContentBox("Graph x: " + xChoice + ", y: " + yChoice, 375, dragListener);

        //Max sizes of XY-coordinates
        double xMax = graph.getxCoordinate().get(graph.getxCoordinate().size() - 1) + 1;
        double yMax = graph.getyCoordinate().get(graph.getyCoordinate().size() - 1) + 1;

        //Axis size
        NumberAxis xAxis = new NumberAxis(0, xMax, graph.tickUnit(xMax));
        NumberAxis yAxis = new NumberAxis(0, yMax, graph.tickUnit(yMax));

        //Axis name
        xAxis.setLabel(Integer.toString(xChoice));
        yAxis.setLabel(Integer.toString(yChoice));

        //Load data into chart
        ScatterChart<Double, Double> scatterChart = new ScatterChart(xAxis, yAxis);
        scatterChart.setData(dataSet);
        scatterChart.setLegendVisible(false);

        //Chart size
        scatterChart.setPrefWidth(375);
        scatterChart.setPrefHeight(300);

        //Pane window
        root.getChildren().add(scatterChart);

        //Set the content of content box to be root
        contentBox.setContent(root);

        return contentBox;
    }

    public void updateGraph(StackPane root, int xChoice, int yChoice) {

        root.getChildren().clear();
        generateGraph(root, xChoice, yChoice);

    }


}
