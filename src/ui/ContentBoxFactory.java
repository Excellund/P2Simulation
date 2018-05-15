package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import simulation.Settings;
import utils.GraphSettings;

import java.util.List;


public class ContentBoxFactory {
    private DragListener dragListener;

    private StackPane root1 = new StackPane();
    private StackPane root2 = new StackPane();
    private StackPane root3 = new StackPane();

    public ContentBoxFactory(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    /*All inputs to navigation box*/
    public ContentBox generateNavigator(double width, ContentBox interactionBox, ContentArea interactionBoxArea2) {
        ContentBox contentBox = new ContentBox("Navigator", width, dragListener);
        Node simLogo = new ImageView(new Image(getClass().getResourceAsStream("logo.png")));
        TreeItem<String> menuRoot = new TreeItem<>("Fish simulation", simLogo);
        TreeView<String> menu = new TreeView<>(menuRoot);
        //Categories for settings
        Node settings = new ImageView(new Image(getClass().getResourceAsStream("settings.png")));
        TreeItem<String> itemSettings = new TreeItem<>("Settings", settings);
        TreeItem<String> itemSimulation = new TreeItem<>("Simulation");
        TreeItem<String> itemFish = new TreeItem<>("Fish");
        TreeItem<String> itemFishery = new TreeItem<>("Fishery");
        TreeItem<String> itemGraph = new TreeItem<>("Graph");
        TreeItem<String> itemGraphics = new TreeItem<>("Graphics");

        itemSettings.getChildren().addAll(itemSimulation, itemFish, itemFishery, itemGraph, itemGraphics);

        //Categories for Navigator
        menuRoot.getChildren().addAll(itemSettings);

        contentBox.setContent(menu);

        //Checking which category that has been pressed, and what contents the window should have
        menu.getSelectionModel().selectedItemProperty().addListener(event ->
        {
            if (menu.getSelectionModel().getSelectedItem().getParent() != null) {
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

                        }
                        break;
                }
            }
        });

        return contentBox;
    }


    //Checking for each type of box, if its already open, if not it will open
    public void interactionBoxChecker(ContentBox interactionBox, ContentArea interactionBoxArea2, TreeView<String> menu) {
        if (interactionBox.getParent() == null && menu.getSelectionModel().selectedItemProperty().getValue().isLeaf()) {
            interactionBoxArea2.getChildren().add(interactionBox);
        }
    }

    //Generate the difference boxes
    public ContentBox generateInteractionBox(double width) {
        ContentBox interactionBox = new ContentBox("Interaction", width, dragListener);

        return interactionBox;
    }

    //Generate the contents for each categories in the difference choices the user has
    public void interactionBoxSetContextSimulation(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Simulation settings");
        //Adding boxes that can contain and post content
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);
        /*Creating labels and texfields for everything, Label for the user to read and Textfield
         * to display and change the current setting value*/
        Label labelNumVessel = new Label("Number of vessels");
        TextField textNumVessel = new TextField(Float.toString(Settings.NUM_VESSELS));
        Label labelPlanktonGrowth = new Label("Growth per time step");
        TextField textPlanktonGrowth = new TextField(Float.toString(Settings.PLANKTON_GROWTH_PER_TIMESTEP));
        Label labelPlanktonMax = new Label("Max plankton");
        TextField textPlanktonMax = new TextField(Float.toString(Settings.MAX_PLANKTON));
        Label labelInitialMaxPlanktonDensity = new Label("Max plankton density");
        TextField textInitialMaxPlanktonDensity = new TextField(Float.toString(Settings.INITIAL_MAX_PLANKTON_DENSITY));
        //Save button that will store the settings, until you close the program
        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.NUM_VESSELS = Float.parseFloat(textNumVessel.getText());
                Settings.PLANKTON_GROWTH_PER_TIMESTEP = Float.parseFloat(textPlanktonGrowth.getText());
                Settings.MAX_PLANKTON = Float.parseFloat(textPlanktonMax.getText());
                Settings.INITIAL_MAX_PLANKTON_DENSITY = Float.parseFloat(textInitialMaxPlanktonDensity.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelNumVessel, labelPlanktonGrowth, labelPlanktonMax, labelInitialMaxPlanktonDensity);
        columnB.getChildren().addAll(textNumVessel, textPlanktonGrowth, textPlanktonMax, textInitialMaxPlanktonDensity);

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
        Label labelEnergy = new Label("Energy per size");
        TextField textEnergy = new TextField(Float.toString(Settings.ENERGY_POINTS_PER_SIZE_POINTS));
        Label labelHealth = new Label("Health per size");
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
        TextField textMutationGaussian = new TextField(Float.toString(Settings.MUTATION_GAUSSIAN_VARIANCE));
        Label labelNutritionSize = new Label("Nutrition per size");
        TextField textNutritionSize = new TextField(Float.toString(Settings.NUTRITION_PER_SIZE_POINT));
        Label labelMinEnergyMating = new Label("Min energy for mating");
        TextField textMinEnergyMating = new TextField(Float.toString(Settings.MIN_ENERGY_MATING));
        Label labelMatingEnergyUse = new Label("Energy used for mating");
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
        Label labelMaxMoveToSpeed = new Label("Max moves to Speed");
        TextField textMaxMoveToSpeed = new TextField(Float.toString(Settings.MAX_MOVES_CORRESPONDING_TO_SPEED));
        Label labelEnergySpeed = new Label("Energy speed correlation");
        TextField textEnergySpeed = new TextField(Float.toString(Settings.ENERGY_SPEED_CORRELATION));
        Label labelMatingDelay = new Label("Mating delay");
        TextField textMatingDelay = new TextField(Float.toString(Settings.MATING_DELAY));
        Label labelVisionRange = new Label("Vision range");
        TextField textVisionRange = new TextField(Float.toString(Settings.VISION_RANGE));
        Label labelFishGrowthTS = new Label("Fish Growth per time step");
        TextField textFishGrowthTS = new TextField(Float.toString(Settings.FISH_GROWTH_RATE_PER_TIMESTEP));
        Label labelFishSizePenalty = new Label("Fish size penalty");
        TextField textFishSizePenalty = new TextField(Float.toString(Settings.FISH_SIZE_PENALTY));
        Label labelFishSpeedPenalty = new Label("Fish speed penalty");
        TextField textFishSpeedPenalty = new TextField(Float.toString(Settings.FISH_SPEED_PENALTY));
        Label labelFishHerbivorePenalty = new Label("Fish herbivore penalty");
        TextField textFishHerbivorePenalty = new TextField(Float.toString(Settings.FISH_HERBIVORE_EFFICIENCY_PENALTY));
        Label labelFishCarnivorePenalty = new Label("Fish carnivore penalty");
        TextField textFishCarnivorePenalty = new TextField(Float.toString(Settings.FISH_CARNIVORE_EFFICIENCY_PENALTY));
        Label labelFishAttackPenalty = new Label("Fish attack penalty");
        TextField textFishAttackPenalty = new TextField(Float.toString(Settings.FISH_ATTACK_ABILITY_PENALTY));


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
                Settings.MUTATION_GAUSSIAN_VARIANCE = Float.parseFloat(textMutationGaussian.getText());
                Settings.NUTRITION_PER_SIZE_POINT = Float.parseFloat(textNutritionSize.getText());
                Settings.MIN_ENERGY_MATING = Float.parseFloat(textMinEnergyMating.getText());
                Settings.MATING_ENERGY_CONSUMPTION = Float.parseFloat(textMatingEnergyUse.getText());
                Settings.HEALTH_REDUCTION_ON_LOW_ENERGY = Float.parseFloat(textHealthReducLowEnergy.getText());
                Settings.MIN_ENERGY_HEALTH_INCREASE = Float.parseFloat(textMinEenergyIncrease.getText());
                Settings.ENERGY_HEALTH_INCREASE = Float.parseFloat(textEnergyHealthIncrease.getText());
                Settings.TIME_BEFORE_HATCH = Float.parseFloat(textTimeToHatch.getText());
                Settings.CARCASS_DECAY_PER_TIMESTEP = Float.parseFloat(textCarcassDecay.getText());
                Settings.MAX_MOVES_CORRESPONDING_TO_SPEED = Float.parseFloat(textMaxMoveToSpeed.getText());
                Settings.ENERGY_SPEED_CORRELATION = Float.parseFloat(textEnergySpeed.getText());
                Settings.MATING_DELAY = Float.parseFloat(textMatingDelay.getText());
                Settings.VISION_RANGE = Float.parseFloat(textVisionRange.getText());
                Settings.FISH_GROWTH_RATE_PER_TIMESTEP = Float.parseFloat(textFishGrowthTS.getText());
                Settings.FISH_SIZE_PENALTY = Float.parseFloat(textFishSizePenalty.getText());
                Settings.FISH_SPEED_PENALTY = Float.parseFloat(textFishSpeedPenalty.getText());
                Settings.FISH_HERBIVORE_EFFICIENCY_PENALTY = Float.parseFloat(textFishHerbivorePenalty.getText());
                Settings.FISH_CARNIVORE_EFFICIENCY_PENALTY = Float.parseFloat(textFishCarnivorePenalty.getText());
                Settings.FISH_ATTACK_ABILITY_PENALTY = Float.parseFloat(textFishAttackPenalty.getText());


            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelFishGrowthTS, labelMaxSize, labelNutritionSize, labelEnergy, labelHealth, labelMinEnergyIncrease, labelEnergyHealthIncrease, labelHealthReducLowEnergy, labelMinPredation, labelEnergyUseAttack, labelMaxDamage, labelMaxMoveToSpeed, labelEnergySpeed, labelVisionRange);
        columnB.getChildren().addAll(textFishGrowthTS, textMaxSize, textNutritionSize, textEnergy, textHealth, textMinEenergyIncrease, textEnergyHealthIncrease, textHealthReducLowEnergy, textMinPredation, textEnergyUseAttack, textMaxDamage, textMaxMoveToSpeed, textEnergySpeed, textVisionRange);
        columnA.getChildren().addAll(labelCompatibilitySteepness, labelCompatibilityMidpoint, labelMinMating, labelMatingEnergyUse, labelMinEnergyMating, labelMatingDelay, labelEnergyEgg, labelTimeToHatch, labelMutationAmount, labelMutationGaussian, labelCarcassDecay);
        columnB.getChildren().addAll(textCompatibilitySteepness, textCompatibilityMidpoint, textMinMating, textMatingEnergyUse, textMinEnergyMating, textMatingDelay, textEnergyEgg, textTimeToHatch, textMutationAmount, textMutationGaussian, textCarcassDecay);
        columnA.getChildren().addAll(labelFishSizePenalty, labelFishSpeedPenalty, labelFishHerbivorePenalty, labelFishCarnivorePenalty, labelFishAttackPenalty);
        columnB.getChildren().addAll(textFishSizePenalty, textFishSpeedPenalty, textFishHerbivorePenalty, textFishCarnivorePenalty, textFishAttackPenalty);


        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void interactionBoxSetContextFishery(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Fishery settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelTravel = new Label("Vessels travel distance");
        TextField textTravel = new TextField(Float.toString(Settings.VESSEL_TRAVEL_DISTANCE));
        Label labelMorphologyMin = new Label("Morphology Min");
        TextField textMorphologyMin = new TextField(Float.toString(Settings.MIN_MORPHOLOGY));
        Label labelMorphologyMax = new Label("Morphology Max");
        TextField textMorphologyMax = new TextField(Float.toString(Settings.MAX_MORPHOLOGY));
        Label labelQuotasMin = new Label("Fishing quotas Min");
        TextField textQuotasMin = new TextField(Float.toString(Settings.FISHING_QUOTAS_MIN));
        Label labelQuotasMax = new Label("Fishing quotas Max");
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


        columnA.getChildren().addAll(labelTravel, labelMorphologyMin, labelMorphologyMax, labelQuotasMin, labelQuotasMax, labelWidthSteepness);
        columnB.getChildren().addAll(textTravel, textMorphologyMin, textMorphologyMax, textQuotasMin, textQuotasMax, textWidthSteepness);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }

    public void interactionBoxSetContextGraphics(ContentBox interactionBox) {
        interactionBox.getToolbar().setTitle("Graphics settings");
        HBox mainContent = new HBox(10);
        VBox columnA = new VBox();
        VBox columnB = new VBox(4);

        mainContent.getChildren().addAll(columnA, columnB);

        Label labelPlanktonGamma = new Label("Plankton gamma");
        TextField textPlanktonGamma = new TextField(Float.toString(Settings.PLANKTON_GAMMA));
        Label labelFishGamma = new Label("Fish gamma");
        TextField textFishGamma = new TextField(Float.toString(Settings.FISH_GAMMA));
        Label labelColourByTendency = new Label("Colour by tendency");
        TextField textColourByTendency = new TextField(Float.toString(Settings.COLOR_BY_TENDENCY));
        Label labelFPS = new Label("Target FPS");
        TextField textFPS = new TextField(Float.toString(Settings.TARGET_FPS));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.PLANKTON_GAMMA = Float.parseFloat(textPlanktonGamma.getText());
                Settings.FISH_GAMMA = Float.parseFloat(textFishGamma.getText());
                Settings.COLOR_BY_TENDENCY = Float.parseFloat(textColourByTendency.getText());
                Settings.TARGET_FPS = Float.parseFloat(textFPS.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        columnA.getChildren().addAll(labelPlanktonGamma, labelFishGamma, labelColourByTendency, labelFPS);
        columnB.getChildren().addAll(textPlanktonGamma, textFishGamma, textColourByTendency, textFPS);
        columnB.getChildren().add(saveButton);

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
        Label labelVesselScale = new Label("Vessel scale");
        TextField textVesselScale = new TextField(Float.toString(Settings.VESSEL_SCALE));

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("buttonContent");
        saveButton.setOnAction(event ->
        {
            try {
                Settings.MORPHOLOGY = Float.parseFloat(textMorphology.getText());
                Settings.QUOTAS = Float.parseFloat(textQuotas.getText());
                Settings.VESSEL_SCALE = Float.parseFloat(textVesselScale.getText());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });


        columnA.getChildren().addAll(labelMorphology, labelQuotas, labelVesselScale);
        columnB.getChildren().addAll(textMorphology, textQuotas, textVesselScale);

        columnB.getChildren().add(saveButton);

        interactionBox.setContent(mainContent);
    }


    public ContentBox[] interactionBoxSetContentGraph(ContentBox interactionBox) {

        HBox mainContent = new HBox(10);
        GridPane grid = new GridPane();
        grid.setVgap(10);

        //ChoiceBox choices
        ObservableList<String> options = FXCollections.observableArrayList("Time Step", "Avg. BWD%", "Avg. Morphology", "Avg. Max Spawning",
                "Avg. Plankton Density", "Avg. Schooling Tendency", "Number of Fish",
                "Number of Carnivores", "Number of Planktivores", "Number of Scavengers",
                "Number of Eggs", "Number of Carcass");

        //Array of content boxes which holds the three graphs
        ContentBox[] graphs = new ContentBox[3];

        //Graph 1
        //Labels
        Label graph1 = new Label("Graph 1");
        Label xData1 = new Label("x plot ");
        Label yData1 = new Label("y plot");

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

        xChoiceBox1.setValue(GraphSettings.CHOICE_ONE_X);
        yChoiceBox1.setValue(GraphSettings.CHOICE_ONE_Y);

        //Chosen value in choice box gets stored in XY-coordinates
        xChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_ONE_X = getChoice(xChoiceBox1));
        xChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_ONE_X = (String) xChoiceBox1.getValue());
        yChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_ONE_Y = getChoice(yChoiceBox1));
        yChoiceBox1.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_ONE_Y = (String) yChoiceBox1.getValue());

        //First graph is generated and stored in the array of graphs
        ContentBox graphOneGenerate = generateGraph(root1, GraphSettings.GRAPH_ONE_X, GraphSettings.GRAPH_ONE_Y, xChoiceBox1, yChoiceBox1);
        graphs[0] = graphOneGenerate;

        //Update action when save button is pressed
        saveButton1.setOnAction(e -> updateGraph(root1, GraphSettings.GRAPH_ONE_X, GraphSettings.GRAPH_ONE_Y, xChoiceBox1, yChoiceBox1));

        //Graph 2
        //Labels
        Label graph2 = new Label("Graph 2");
        Label xData2 = new Label("x plot ");
        Label yData2 = new Label("y plot");

        // Choice boxes
        final ChoiceBox xChoiceBox2 = new ChoiceBox(options);
        final ChoiceBox yChoiceBox2 = new ChoiceBox(options);

        xChoiceBox2.setValue(GraphSettings.CHOICE_TWO_X);
        yChoiceBox2.setValue(GraphSettings.CHOICE_TWO_Y);

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
        xChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_TWO_X = getChoice(xChoiceBox2));
        xChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_TWO_X = (String) xChoiceBox2.getValue());
        yChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_TWO_Y = getChoice(yChoiceBox2));
        yChoiceBox2.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_TWO_Y = (String) yChoiceBox2.getValue());


        //Second graph is generated and stored in the array of graphs
        ContentBox graphTwoGenerate = generateGraph(root2, GraphSettings.GRAPH_TWO_X, GraphSettings.GRAPH_TWO_Y, xChoiceBox2, yChoiceBox2);
        graphs[1] = graphTwoGenerate;

        //Update action when save button is pressed
        saveButton2.setOnAction(e -> updateGraph(root2, GraphSettings.GRAPH_TWO_X, GraphSettings.GRAPH_TWO_Y, xChoiceBox2, yChoiceBox2));

        //Graph 3
        //Labels
        Label graph3 = new Label("Graph 3");
        Label xData3 = new Label("x plot ");
        Label yData3 = new Label("y plot");

        //Choice boxes
        final ChoiceBox xChoiceBox3 = new ChoiceBox(options);
        final ChoiceBox yChoiceBox3 = new ChoiceBox(options);

        xChoiceBox3.setValue(GraphSettings.CHOICE_THREE_X);
        yChoiceBox3.setValue(GraphSettings.CHOICE_THREE_Y);

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
        xChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_THREE_X = getChoice(xChoiceBox3));
        xChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_THREE_X = (String) xChoiceBox3.getValue());
        yChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.GRAPH_THREE_Y = getChoice(yChoiceBox3));
        yChoiceBox3.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> GraphSettings.CHOICE_THREE_Y = (String) yChoiceBox3.getValue());

        //Third graph is generated and stored in the array of graphs
        ContentBox graphThreeGenerate = generateGraph(root3, GraphSettings.GRAPH_THREE_X, GraphSettings.GRAPH_THREE_Y, xChoiceBox3, yChoiceBox3);
        graphs[2] = graphThreeGenerate;

        //Update action when save button is pressed
        saveButton3.setOnAction(e -> updateGraph(root3, GraphSettings.GRAPH_THREE_X, GraphSettings.GRAPH_THREE_Y, xChoiceBox3, yChoiceBox3));

        //Load data file
        TextField textFileNumber = new TextField("File number...");
        textFileNumber.setMaxWidth(100);
        textFileNumber.setOnMouseClicked(event -> textFileNumber.clear());

        Button loadFileChoice = new Button("Load");
        loadFileChoice.getStyleClass().add("buttonContent");
        loadFileChoice.setOnAction(event -> updateFileAndGraphs(
                xChoiceBox1, yChoiceBox1,
                xChoiceBox2, yChoiceBox2,
                xChoiceBox3, yChoiceBox3,
                textFileNumber));

        grid.add(textFileNumber, 3, 8);
        grid.add(loadFileChoice, 4, 8);

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

        if (choice.equals("Time Step")) {
            choiceValue = 0;
        } else if (choice.equals("Avg. BWD%")) {
            choiceValue = 1;
        } else if (choice.equals("Avg. Morphology")) {
            choiceValue = 2;
        } else if (choice.equals("Avg. Max Spawning")) {
            choiceValue = 3;
        } else if (choice.equals("Avg. Plankton Density")) {
            choiceValue = 4;
        } else if (choice.equals("Avg. Schooling Tendency")) {
            choiceValue = 5;
        } else if (choice.equals("Number of Fish")) {
            choiceValue = 6;
        } else if (choice.equals("Number of Carnivores")) {
            choiceValue = 7;
        } else if (choice.equals("Number of Planktivores")) {
            choiceValue = 8;
        } else if (choice.equals("Number of Scavengers")) {
            choiceValue = 9;
        } else if (choice.equals("Number of Eggs")) {
            choiceValue = 10;
        } else if (choice.equals("Number of Carcass")) {
            choiceValue = 11;
        }

        return choiceValue;
    }

    public ContentBox generateGraph(StackPane root, int xChoice, int yChoice, ChoiceBox xChoiceBox, ChoiceBox yChoiceBox) {

        Graph graph = new Graph();

        ObservableList<XYChart.Series<Double, Double>> dataSet = graph.getChartData(xChoice, yChoice);

        //Name of content box
        ContentBox contentBox = new ContentBox("Graph", 375, dragListener);

        //Max and minimum size of XY-coordinates
        double xMax = 0;
        double yMax = 0;

        for (int i = 0; i < graph.getxCoordinate().size(); i++) {
            if (graph.getxCoordinate().get(i) > xMax) {
                xMax = graph.getxCoordinate().get(i);
            }

            if (graph.getyCoordinate().get(i) > yMax) {
                yMax = graph.getyCoordinate().get(i);
            }
        }

        double xMin = xMax;
        double yMin = yMax;

        for (int i = 0; i < graph.getxCoordinate().size(); i++) {
            if (graph.getxCoordinate().get(i) < xMin) {
                xMin = graph.getxCoordinate().get(i);
            }

            if (graph.getyCoordinate().get(i) < yMin) {
                yMin = graph.getyCoordinate().get(i);
            }
        }

        //Axis size
        NumberAxis xAxis = new NumberAxis(xMin - 1, xMax + 1, graph.tickUnit(xMax));
        NumberAxis yAxis = new NumberAxis(yMin - 1, yMax + 1, graph.tickUnit(yMax));

        //Axis name
        xAxis.setLabel((String) xChoiceBox.getValue());
        yAxis.setLabel((String) yChoiceBox.getValue());

        //Load data into chart
        ScatterChart<Double, Double> scatterChart = new ScatterChart(xAxis, yAxis);
        scatterChart.setData(dataSet);
        scatterChart.setLegendVisible(false);

        //Chart size
        scatterChart.setPrefWidth(375);
        scatterChart.setPrefHeight(250);

        //Pane window
        root.getChildren().add(scatterChart);

        //Set the content of content box to be root
        contentBox.setContent(root);

        return contentBox;
    }

    public void updateGraph(StackPane root, int xChoice, int yChoice, ChoiceBox choiceBox1, ChoiceBox choiceBox2) {

        root.getChildren().clear();
        generateGraph(root, xChoice, yChoice, choiceBox1, choiceBox2);

    }

    // Update all graphs to be the data from the new file
    public void updateFileAndGraphs(ChoiceBox choiceBox1, ChoiceBox choiceBox2,
                                    ChoiceBox choiceBox3, ChoiceBox choiceBox4,
                                    ChoiceBox choiceBox5, ChoiceBox choiceBox6, TextField textField) {

        GraphSettings.FILE_NUMBER = Integer.parseInt(textField.getText());
        updateGraph(root1, GraphSettings.GRAPH_ONE_X, GraphSettings.GRAPH_ONE_Y, choiceBox1, choiceBox2);
        updateGraph(root2, GraphSettings.GRAPH_TWO_X, GraphSettings.GRAPH_TWO_Y, choiceBox3, choiceBox4);
        updateGraph(root3, GraphSettings.GRAPH_THREE_X, GraphSettings.GRAPH_THREE_Y, choiceBox5, choiceBox6);

    }


}
