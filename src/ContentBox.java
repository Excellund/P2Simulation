import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ContentBox extends VBox {
    private DragListener dragListener;
    private ScrollPane container;
    private Toolbar toolbar;
    private boolean hide;

    public ContentBox(String title, double width, DragListener dragListener) {
        hide = false;
        VBox.setVgrow(this, Priority.ALWAYS);

        toolbar = new Toolbar(title, width, "toolbar");
        container = new ScrollPane();
        container.getStyleClass().add("content");
        container.setFitToWidth(true);

        this.getStyleClass().add("content-box");
        this.getChildren().addAll(toolbar, container);
        this.setPrefWidth(width);
        this.dragListener = dragListener;

        toolbar.setOnDragDetected(event ->
        {
            dragListener.dragged = this;
            dragListener.parent = (ContentArea) this.getParent(); //might want to revise this

            Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            ClipboardContent clipboardContent = new ClipboardContent();

            snapshotParameters.setFill(Color.TRANSPARENT);
            dragboard.setDragView(this.snapshot(snapshotParameters, null));
            clipboardContent.putString("");
            dragboard.setContent(clipboardContent);

            event.consume();
        });

        this.setOnDragOver(event ->
        {
            event.acceptTransferModes(TransferMode.MOVE);

            event.consume();
        });

        this.setOnDragDropped(event ->
        {
            if (dragListener.dragged != this) {
                dragListener.parent.getChildren().remove(dragListener.dragged);

                int index = this.getParent().getChildrenUnmodifiable().indexOf(this);

                if (event.getY() <= this.getHeight() / 2) {
                    ((ContentArea) this.getParent()).getChildren().add(index, dragListener.dragged);
                } else {
                    ((ContentArea) this.getParent()).getChildren().add(index + 1, dragListener.dragged);
                }
            }

            event.consume();
        });

        Button toggle = new Button("-");
        Button remove = new Button("x");

        toggle.setOnMouseClicked(event ->
        {
            hide = !hide;

            if (hide) {
                VBox.setVgrow(this, Priority.NEVER);
                this.getChildren().remove(container);
            } else {
                VBox.setVgrow(this, Priority.ALWAYS);
                this.getChildren().add(container);
            }
        });

        remove.setOnMouseClicked(event ->
        {
            ((ContentArea) this.getParent()).getChildren().remove(this);
        });

        toolbar.addButton(toggle);
        toolbar.addButton(remove);
    }

    public void setContent(Node content) {
        container.setContent(content);
    }
}
