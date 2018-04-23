package UI;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ContentBoxFactory {
    private DragListener dragListener;

    public ContentBoxFactory(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public ContentBox generateNavigator(double width) {
        ContentBox contentBox = new ContentBox("Navigator", width, dragListener);
        TreeItem<String> menuRoot = new TreeItem<>("simulation name");
        TreeView<String> menu = new TreeView<>(menuRoot);

        TreeItem<String> itemSettings = new TreeItem<>("Settings");
        TreeItem<String> itemFish = new TreeItem<>("Fish");
        TreeItem<String> itemFishery = new TreeItem<>("Fishery");
        TreeItem<String> itemGeneral = new TreeItem<>("General");

        itemSettings.getChildren().addAll(itemFish, itemFishery, itemGeneral);

        TreeItem<String> itemSnapshot = new TreeItem<>("Snapshot");

        menuRoot.getChildren().addAll(itemSettings, itemSnapshot);

        contentBox.setContent(menu);

        return contentBox;
    }
}
