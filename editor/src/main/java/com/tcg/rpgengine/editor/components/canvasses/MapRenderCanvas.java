package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.common.data.maps.MapLayer;
import com.tcg.rpgengine.common.data.maps.MapLayerCell;
import com.tcg.rpgengine.common.data.misc.Float2;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

public class MapRenderCanvas extends ResizableCanvas {

    private final MapEntity map;
    private final Map<UUID, Image> tilesetImages;
    private final Float2 mapTileSize;
    private final Float2 mapTotalPixels;
    private final SimpleDoubleProperty scale;
    private final RowColumnPair cursor;

    private final MapEditor editor;

    public MapRenderCanvas(AssetLibrary assetLibrary, MapEntity map, MapEditor editor) {
        super();
        this.map = map;
        this.editor = editor;
        this.tilesetImages = new HashMap<>();

        map.getAllTilesetIds()
                .stream()
                .map(assetLibrary::getTilesetAssetById)
                .forEach(tiledImageAsset -> {
                    final FileHandle projectFile = ApplicationContext.context().currentProject.getProjectFileHandle();
                    final FileHandle imageFileHandle = projectFile.sibling(tiledImageAsset.getPath());
                    this.tilesetImages.put(tiledImageAsset.id, new Image(imageFileHandle.read()));
                });

        this.mapTileSize = map.getTileSize(assetLibrary, tiledImageAsset -> {
            final Image image = this.tilesetImages.get(tiledImageAsset.id);
            return Float2.of((float) image.getWidth(), (float) image.getHeight());
        });
        final RowColumnPair mapSize = map.getMapSize();
        final float mapWidth = this.mapTileSize.x * mapSize.column;
        final float mapHeight = this.mapTileSize.y * mapSize.row;
        this.mapTotalPixels = Float2.of(mapWidth, mapHeight);

        this.cursor = RowColumnPair.of(-1, -1);

        this.setOnMouseMoved(event -> {
            this.updateCursor(event.getX(), event.getY());
            this.draw();
        });

        this.setOnMousePressed(event -> {
            this.updateCursor(event.getX(), event.getY());
            if (this.cursor.row >= 0 && this.cursor.column >= 0) {
                if (event.isPrimaryButtonDown()) {
                    this.editor.paintSelection(this.cursor);
                } else if (event.isSecondaryButtonDown()) {
                    this.editor.eraseSelection(this.cursor);
                } else if (event.isMiddleButtonDown()) {
                    this.editor.floodFill(this.cursor);
                }
            }
            this.draw();
        });

        this.setOnMouseDragged(event -> {
            final RowColumnPair originalCursor = this.cursor.copy();
            this.updateCursor(event.getX(), event.getY());
            if ((this.cursor.row >= 0 && this.cursor.column >= 0)
                    && (this.cursor.row != originalCursor.row || this.cursor.column != originalCursor.column)) {
                if (event.isPrimaryButtonDown()) {
                    this.editor.paintSelection(this.cursor);
                } else if (event.isSecondaryButtonDown()) {
                    this.editor.eraseSelection(this.cursor);
                }
            }
            this.draw();
        });

        this.setWidth(mapWidth);
        this.setHeight(mapHeight);

        this.scale = new SimpleDoubleProperty(1f);
        this.scale.addListener((observable, oldValue, newValue) -> {
            if (Double.compare(newValue.doubleValue(), 0.25) < 0 || Double.compare(newValue.doubleValue(), 3) > 0) {
                this.scale.set(oldValue.doubleValue());
            } else {
                this.setWidth(this.mapTotalPixels.x * newValue.doubleValue());
                this.setHeight(this.mapTotalPixels.y * newValue.doubleValue());
            }
        });
    }

    private void updateCursor(double x, double y) {
        if (x > 0 && x < this.getWidth() & y > 0 && y < this.getHeight()) {
            final RowColumnPair mapSize = this.map.getMapSize();
            final double tileWidth = this.getWidth() / mapSize.column;
            final double tileHeight = this.getHeight() / mapSize.row;
            final int column = (int) (x / tileWidth);
            final int row = (int) (y / tileHeight);
            if (row >= 0 && row < mapSize.column && column >= 0 && column < mapSize.column) {
                this.cursor.row = row;
                this.cursor.column = column;
            } else {
                this.cursor.row = -1;
                this.cursor.column = -1;
            }
        } else {
            this.cursor.row = -1;
            this.cursor.column = -1;
        }
    }

    @Override
    protected void draw() {
        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        final List<MapLayer> layers = this.map.getLayers();
        Float2 size = Float2.of((float) this.getWidth(), (float) this.getHeight());
        final RowColumnPair mapSize = this.map.getMapSize();
        Float2 destTileSize = Float2.of(size.x / mapSize.column, size.y / mapSize.row);
        for (int i = 0; i < layers.size(); i++) {
            MapLayer layer = layers.get(i);
            this.drawLayer(gc, destTileSize, layer);
            if (i == this.editor.getSelectedLayer()) {
                this.drawSelection(gc, mapSize, destTileSize);
            }
        }


    }

    private void drawSelection(GraphicsContext gc, RowColumnPair mapSize, Float2 destTileSize) {
        if (this.cursor.row >= 0 && this.cursor.column >= 0) {
            gc.setGlobalAlpha(0.75);
            this.editor.getSelectedTileset().ifPresent(selectedTileset -> {
                final RowColumnPair selectionTopLeft = this.editor.getSelectionTopLeft();
                final RowColumnPair selectionBottomRight = this.editor.getSelectionBottomRight();
                int selectionWidth = selectionBottomRight.column - selectionTopLeft.column + 1;
                int selectionHeight = selectionBottomRight.row - selectionTopLeft.row + 1;
                if (this.cursor.column + selectionWidth > mapSize.column) {
                    selectionWidth = mapSize.column - this.cursor.column;
                }
                if (this.cursor.row + selectionHeight > mapSize.row) {
                    selectionHeight = mapSize.row - this.cursor.row;
                }
                final Image tileset = this.tilesetImages.get(selectedTileset);
                for (int i = 0; i < selectionWidth; i++) {
                    for (int j = 0; j < selectionHeight; j++) {
                        final int mapRow = this.cursor.row + j;
                        final int mapColumn = this.cursor.column + i;
                        final int tilesetRow = selectionTopLeft.row + j;
                        final int tilesetColumn = selectionTopLeft.column + i;

                        final float srcX = tilesetColumn * this.mapTileSize.x;
                        final float srcY = tilesetRow * this.mapTileSize.y;

                        final float destX = mapColumn * destTileSize.x;
                        final float destY = mapRow * destTileSize.y;

                        gc.drawImage(tileset,
                                srcX, srcY, this.mapTileSize.x, this.mapTileSize.y,
                                destX, destY, destTileSize.x, destTileSize.y);

                    }
                }
            });
            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawLayer(GraphicsContext gc, Float2 destTileSize, MapLayer layer) {
        final List<MapLayerCell> cells = layer.getCells();
        for (MapLayerCell cell : cells) {
            this.drawCell(gc, destTileSize, cell);

        }
    }

    private void drawCell(GraphicsContext gc, Float2 destTileSize, MapLayerCell cell) {
        final UUID tilesetId = cell.getTilesetId();
        final RowColumnPair mapCoordinate = cell.getMapCoordinate();
        final RowColumnPair tilesetCoordinate = cell.getTilesetCoordinate();
        final Image tileset = this.tilesetImages.get(tilesetId);

        final float srcX = tilesetCoordinate.column * this.mapTileSize.x;
        final float srcY = tilesetCoordinate.row * this.mapTileSize.y;

        final float destX = mapCoordinate.column * destTileSize.x;
        final float destY = mapCoordinate.row * destTileSize.y;

        gc.drawImage(tileset,
                srcX, srcY, this.mapTileSize.x, this.mapTileSize.y,
                destX, destY, destTileSize.x, destTileSize.y);
    }

    public void setScale(double scale) {
        this.scale.set(scale);
    }

    public DoubleProperty scaleProperty() {
        return this.scale;
    }

}
