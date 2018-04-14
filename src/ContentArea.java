import javafx.scene.effect.InnerShadow;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class ContentArea extends VBox {
    private DragListener dragListener;

    public ContentArea(double width, double height, DragListener dragListener) {
        this.dragListener = dragListener;
        this.setPrefWidth(width);
        this.setPrefHeight(height);
        this.getStyleClass().add("box");

        this.setOnDragEntered(event ->
        {
            this.setEffect(new InnerShadow());

            event.consume();
        });

        this.setOnDragExited(event ->
        {
            this.setEffect(null);

            event.consume();
        });

        this.setOnDragOver(event ->
        {
            event.acceptTransferModes(TransferMode.MOVE);

            event.consume();
        });

        this.setOnDragDropped(event ->
        {
            dragListener.parent.getChildren().remove(dragListener.dragged);
            this.getChildren().add(dragListener.dragged);

            event.consume();
        });
    }
}
