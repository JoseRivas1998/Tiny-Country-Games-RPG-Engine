package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.canvasses.CharacterCellPreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Button;

import java.util.UUID;

public class CharacterCellButton extends Button {

    private final CharacterCellPreviewCanvas cellCanvas;

    public CharacterCellButton(AssetLibrary assetLibrary, UUID spritesheetPageId, int charRow, int charColumn,
                               int cellRow, int cellColumn) {
        super();
        final TiledImageAsset iconPage = assetLibrary.getSpritesheetPageAssetById(spritesheetPageId);
        this.cellCanvas = new CharacterCellPreviewCanvas(iconPage, charRow, charColumn, cellRow, cellColumn);
        final Dimension2D cellSize = this.cellCanvas.getIconSize();
        this.cellCanvas.setWidth(cellSize.getWidth() + ApplicationContext.Constants.PADDING * 2);
        this.cellCanvas.setHeight(cellSize.getHeight() + ApplicationContext.Constants.PADDING * 2);

        this.setGraphic(this.cellCanvas);
    }

    public void updateIcon(AssetLibrary assetLibrary, UUID iconPageId, int row, int column,
                           int cellRow, int cellColumn) {
        final TiledImageAsset spritesheet = assetLibrary.getSpritesheetPageAssetById(iconPageId);
        this.cellCanvas.updateImageAsset(spritesheet, row, column, cellRow, cellColumn);
        final Dimension2D iconSize = this.cellCanvas.getIconSize();
        this.cellCanvas.setWidth(iconSize.getWidth() + ApplicationContext.Constants.PADDING * 2);
        this.cellCanvas.setHeight(iconSize.getHeight() + ApplicationContext.Constants.PADDING * 2);
    }

}
