import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import simulation.Settings;
import ui.UISetup;

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
