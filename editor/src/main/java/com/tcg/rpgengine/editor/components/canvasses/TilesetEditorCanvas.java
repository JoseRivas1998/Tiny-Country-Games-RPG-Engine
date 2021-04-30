package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.UUID;

public class TilesetEditorCanvas extends ResizableCanvas {


    private final Image image;
    private final RowColumnPair tilesetSize;

    private final RowColumnPair selectionTopLeft;
    private final RowColumnPair selectionBottomRight;
    private final RowColumnPair selectionStart;

    private final MapEditor editor;
    private final UUID tilesetId;

    public TilesetEditorCanvas(UUID tilesetId, MapEditor editor) {
        super();
        this.editor = editor;
        final ApplicationContext context = ApplicationContext.context();
        final TiledImageAsset tilesetAsset = context.currentProject.assetLibrary.getTilesetAssetById(tilesetId);
        this.tilesetId = tilesetAsset.id;
        final FileHandle projectFileHandle = context.currentProject.getProjectFileHandle();
        final FileHandle tilesetFile = projectFileHandle.sibling(tilesetAsset.getPath());
        this.tilesetSize = RowColumnPair.of(tilesetAsset.rows, tilesetAsset.columns);
        this.image = new Image(tilesetFile.read());
        this.selectionTopLeft = RowColumnPair.of(0, 0);
        this.selectionBottomRight = RowColumnPair.of(0, 0);
        this.selectionStart = RowColumnPair.of(0, 0);
        this.setWidth(this.image.getWidth());
        this.setHeight(this.image.getHeight());

        this.setOnMousePressed(event -> {
            if (event.getX() > 0 && event.getX() < this.getWidth()
                    && event.getY() > 0 && event.getY() < this.getHeight()) {
                final double tileWidth = this.getWidth() / this.tilesetSize.column;
                final double tileHeight = this.getHeight() / this.tilesetSize.row;
                final int column = (int) (event.getX() / tileWidth);
                final int row = (int) (event.getY() / tileHeight);
                this.selectionTopLeft.row = row;
                this.selectionTopLeft.column = column;

                this.selectionBottomRight.row = row;
                this.selectionBottomRight.column = column;

                this.selectionStart.row = row;
                this.selectionStart.column = column;
                this.draw();
            }
        });

        this.setOnMouseDragged(event -> {
            final double tileWidth = this.getWidth() / this.tilesetSize.column;
            final double tileHeight = this.getHeight() / this.tilesetSize.row;
            final int column = Math.min(Math.max((int) (event.getX() / tileWidth), 0), this.tilesetSize.column - 1);
            final int row = Math.min(Math.max((int) (event.getY() / tileHeight), 0), this.tilesetSize.row - 1);

            this.selectionTopLeft.row = Math.min(row, this.selectionStart.row);
            this.selectionTopLeft.column = Math.min(column, this.selectionStart.column);

            this.selectionBottomRight.row = Math.max(row, this.selectionStart.row);
            this.selectionBottomRight.column = Math.max(column, this.selectionStart.column);

            this.draw();

        });

        this.setOnMouseReleased(event -> {
            final double tileWidth = this.getWidth() / this.tilesetSize.column;
            final double tileHeight = this.getHeight() / this.tilesetSize.row;
            final int column = Math.min(Math.max((int) (event.getX() / tileWidth), 0), this.tilesetSize.column - 1);
            final int row = Math.min(Math.max((int) (event.getY() / tileHeight), 0), this.tilesetSize.row - 1);

            this.selectionTopLeft.row = Math.min(row, this.selectionStart.row);
            this.selectionTopLeft.column = Math.min(column, this.selectionStart.column);

            this.selectionBottomRight.row = Math.max(row, this.selectionStart.row);
            this.selectionBottomRight.column = Math.max(column, this.selectionStart.column);

            this.editor.setSelection(this.tilesetId, this.selectionTopLeft, this.selectionBottomRight);

            this.draw();
        });

    }

    @Override
    protected void draw() {
        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        gc.drawImage(this.image, 0, 0);

        gc.setStroke(Color.web("#777777FF"));
        gc.setLineWidth(0.75);

        final double tileWidth = this.getWidth() / this.tilesetSize.column;
        final double tileHeight = this.getHeight() / this.tilesetSize.row;

        for (int row = 0; row <= this.tilesetSize.row; row++) {
            final double lineY = row * tileHeight;
            gc.strokeLine(0, lineY, this.getWidth(), lineY);
        }
        for (int col = 0; col <= this.tilesetSize.column; col++) {
            final double lineX = col * tileWidth;
            gc.strokeLine(lineX, 0, lineX, this.getHeight());
        }

        gc.setStroke(Color.web("#00ffff"));
        gc.setLineWidth(2.0);

        final double selectionX = this.selectionTopLeft.column * tileWidth;
        final double selectionY = this.selectionTopLeft.row * tileHeight;
        final double selectionWidth = (this.selectionBottomRight.column - this.selectionTopLeft.column + 1) * tileWidth;
        final double selectionHeight = (this.selectionBottomRight.row - this.selectionTopLeft.row + 1) * tileHeight;
        gc.strokeRect(selectionX, selectionY, selectionWidth, selectionHeight);

    }

    public void resetSelection() {
        this.selectionTopLeft.row = 0;
        this.selectionTopLeft.column = 0;
        this.selectionBottomRight.row = 0;
        this.selectionBottomRight.column = 0;
        this.editor.setSelection(this.tilesetId, this.selectionTopLeft, this.selectionBottomRight);
        this.draw();
    }
}
