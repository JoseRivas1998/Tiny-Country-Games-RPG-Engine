package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.entities.Actor;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.components.canvasses.CharacterCellPreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.UUID;

public class ActorListView extends ListView<Actor> {

    public ActorListView() {
        super();
        this.setCellFactory(param -> new ActorCell());
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private static class ActorCell extends ListCell<Actor> {
        @Override
        protected void updateItem(Actor item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
                final UUID spritesheetPageId = item.getSpritesheetPageId();
                final TiledImageAsset spritesheet = assetLibrary.getSpritesheetPageAssetById(spritesheetPageId);
                final RowColumnPair characterIndex = item.getCharacterIndex();
                final int row = characterIndex.row;
                final int column = characterIndex.column;
                final CharacterCellPreviewCanvas characterCellCanvas = new CharacterCellPreviewCanvas(spritesheet,
                        row, column, 0, 1
                );
                final Dimension2D iconSize = characterCellCanvas.getIconSize();
                characterCellCanvas.setWidth(iconSize.getWidth());
                characterCellCanvas.setHeight(iconSize.getHeight());
                final Label label = new Label(item.getName());
                label.setGraphic(characterCellCanvas);
                this.setGraphic(label);
            } else {
                this.setGraphic(null);
            }
        }
    }

}
