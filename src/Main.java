import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import simulation.Engine;
import simulation.Settings;
import simulation.Simulation;
import simulation.Snapshot;
import ui.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    public static void main(String[] args) {
        Settings.defaultAbbreviated();
        Settings.useAbbreviated();
        Settings.toFile("default");
        Settings.fromFile("default");
        launch(args);

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = UISetup.getScene(primaryStage);

        //initialize the main window
        scene.getStylesheets().add("body.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
