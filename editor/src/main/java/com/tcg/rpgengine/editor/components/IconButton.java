package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.IconCell;
import com.tcg.rpgengine.editor.components.canvasses.TileCellPreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Button;

import java.util.UUID;

public class IconButton extends Button {

    private final TileCellPreviewCanvas iconCanvas;

    public IconButton(AssetLibrary assetLibrary, UUID iconPageId, int row, int column) {
        super();
        final TiledImageAsset iconPage = assetLibrary.getIconPageById(iconPageId);
        this.iconCanvas = new TileCellPreviewCanvas(iconPage, row, column);
        final Dimension2D iconSize = this.iconCanvas.getIconSize();
        this.iconCanvas.setWidth(iconSize.getWidth() + ApplicationContext.Constants.PADDING * 2);
        this.iconCanvas.setHeight(iconSize.getHeight() + ApplicationContext.Constants.PADDING * 2);

        this.setGraphic(this.iconCanvas);
    }

    public void updateIcon(AssetLibrary assetLibrary, UUID iconPageId, int row, int column) {
        this.iconCanvas.updateImageAsset(assetLibrary.getIconPageById(iconPageId), row, column);
        final Dimension2D iconSize = this.iconCanvas.getIconSize();
        this.iconCanvas.setWidth(iconSize.getWidth() + ApplicationContext.Constants.PADDING * 2);
        this.iconCanvas.setHeight(iconSize.getHeight() + ApplicationContext.Constants.PADDING * 2);
    }

}
