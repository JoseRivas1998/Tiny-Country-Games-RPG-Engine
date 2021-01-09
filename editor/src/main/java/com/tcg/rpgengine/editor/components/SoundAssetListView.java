package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class SoundAssetListView extends ListView<SoundAsset> {

    public SoundAssetListView() {
        super();
        this.setCellFactory(param -> new SoundAssetCell());
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private static class SoundAssetCell extends ListCell<SoundAsset> {
        @Override
        protected void updateItem(SoundAsset item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                final Label soundTitle = new Label(item.title);
                soundTitle.setStyle("-fx-font-size: 1.25em;");
                final Label soundPath = new Label(item.path);
                final VBox vBox = new VBox(ApplicationContext.Constants.SPACING);
                vBox.getChildren().addAll(soundTitle, soundPath);
                this.setGraphic(vBox);
            }
        }
    }

}
