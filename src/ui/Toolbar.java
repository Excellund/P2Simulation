package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Toolbar extends HBox {
    private Label title;
    private HBox buttonBar;

    public Toolbar(String title, double width, String styleClass) {
        Pane spacer = new Pane();
        buttonBar = new HBox(2);

        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getStyleClass().add("button-box");

        this.setPrefWidth(width);
        this.title = new Label(title);
        this.getChildren().addAll(this.title, spacer, buttonBar);
        this.getStyleClass().add(styleClass);
        this.setSpacing(2);

        HBox.setHgrow(spacer, Priority.ALWAYS);
    }

    public void addButton(Button button) {
        buttonBar.getChildren().add(button);
    }

    //access private fields

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle() {
        return title.getText();
    }
}
