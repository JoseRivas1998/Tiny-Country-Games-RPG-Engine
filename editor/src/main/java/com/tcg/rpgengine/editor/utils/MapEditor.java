package com.tcg.rpgengine.editor.utils;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.common.data.maps.MapLayer;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.UUID;

public class MapEditor {

    private MapEntity map;
    private UUID selectedTileset;

    private final ReadOnlyIntegerWrapper tilesetSelectionAreaProperty;
    private final RowColumnPair selectionTopLeft;
    private final RowColumnPair selectionBottomRight;

    private final ReadOnlyBooleanWrapper mapUpdated;

    private int selectedLayer;

    public MapEditor() {
        this.selectedTileset = null;
        this.tilesetSelectionAreaProperty = new ReadOnlyIntegerWrapper(1);
        this.mapUpdated = new ReadOnlyBooleanWrapper(false);
        this.selectionTopLeft = RowColumnPair.of(0, 0);
        this.selectionBottomRight = RowColumnPair.of(0, 0);
        this.selectedLayer = -1;
    }

    public Optional<UUID> getSelectedTileset() {
        return Optional.ofNullable(this.selectedTileset);
    }

    public RowColumnPair getSelectionTopLeft() {
        if (this.map == null || this.selectedTileset == null) return null;
        return this.selectionTopLeft.copy();
    }

    public RowColumnPair getSelectionBottomRight() {
        if (this.map == null || this.selectedTileset == null) return null;
        return this.selectionBottomRight.copy();
    }

    public void setSelection(UUID tileSetId, RowColumnPair topLet, RowColumnPair bottomRight) {
        if (this.map == null) return;
        final ApplicationContext context = ApplicationContext.context();
        final TiledImageAsset tileset = context.currentProject.assetLibrary.getTilesetAssetById(tileSetId);
        this.selectedTileset = tileset.id;
        final int minRow = Math.min(topLet.row, bottomRight.row);
        final int minColumn = Math.min(topLet.column, bottomRight.column);
        final int maxRow = Math.max(topLet.row, bottomRight.row);
        final int maxColumn = Math.max(topLet.column, bottomRight.column);

        this.selectionTopLeft.row = Math.min(Math.max(minRow, 0), tileset.rows - 1);
        this.selectionTopLeft.column = Math.min(Math.max(minColumn, 0), tileset.columns - 1);

        this.selectionBottomRight.row = Math.min(Math.max(maxRow, 0), tileset.rows - 1);
        this.selectionBottomRight.column = Math.min(Math.max(maxColumn, 0), tileset.columns - 1);

        final int selectionWidth = this.selectionBottomRight.column - this.selectionTopLeft.column + 1;
        final int selectionHeight = this.selectionBottomRight.row - this.selectionTopLeft.row + 1;
        this.tilesetSelectionAreaProperty.set(selectionWidth * selectionHeight);
    }

    public ReadOnlyIntegerProperty tilesetSelectionAreaProperty() {
        return this.tilesetSelectionAreaProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty mapUpdatedProperty() {
        return this.mapUpdated.getReadOnlyProperty();
    }

    public void setMap(MapEntity map) {
        this.map = map;
        this.mapUpdated.set(false);
    }

    public void selectLayer(int layer) {
        if (this.map == null) return;
        if (layer < 0 || layer >= this.map.getLayers().size()) return;
        this.selectedLayer = layer;
    }

    public int getSelectedLayer() {
        return this.selectedLayer;
    }

    public void paintSelection(RowColumnPair position) {
        if (this.map == null || this.selectedTileset == null) return;
        if (this.selectedLayer < 0 || this.selectedLayer >= this.map.getLayers().size()) return;
        final RowColumnPair mapSize = this.map.getMapSize();
        if (position.row < 0 || position.row >= mapSize.row
                || position.column < 0 || position.column >= mapSize.column) {
            return;
        }
        int selectionWidth = this.selectionBottomRight.column - this.selectionTopLeft.column + 1;
        int selectionHeight = this.selectionBottomRight.row - this.selectionTopLeft.row + 1;
        if (position.column + selectionWidth > mapSize.column) {
            selectionWidth = mapSize.column - position.column;
        }
        if (position.row + selectionHeight > mapSize.row) {
            selectionHeight = mapSize.row - position.row;
        }
        final MapLayer mapLayer = this.map.getLayers().get(this.selectedLayer);
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        for (int i = 0; i < selectionWidth; i++) {
            for (int j = 0; j < selectionHeight; j++) {
                final RowColumnPair mapCoordinate = RowColumnPair.of(position.row + j, position.column + i);
                final RowColumnPair tilesetCoordinate = RowColumnPair.of(
                        this.selectionTopLeft.row + j, this.selectionTopLeft.column + i
                );
                mapLayer.putCell(assetLibrary, this.selectedTileset, mapCoordinate, tilesetCoordinate);
                this.mapUpdated.set(true);
            }
        }
    }

    public void floodFill(RowColumnPair position) {
        if (this.map == null || this.selectedTileset == null) return;
        if (this.selectedLayer < 0 || this.selectedLayer >= this.map.getLayers().size()) return;
        final RowColumnPair mapSize = this.map.getMapSize();
        if (position.row < 0 || position.row >= mapSize.row
                || position.column < 0 || position.column >= mapSize.column) {
            return;
        }
        if (this.tilesetSelectionAreaProperty.get() != 1) return;
        final MapLayer mapLayer = this.map.getLayers().get(this.selectedLayer);
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final RowColumnPair mapCoordinate = RowColumnPair.of(position.row, position.column);
        final RowColumnPair tilesetCoordinate = RowColumnPair.of(
                this.selectionTopLeft.row, this.selectionTopLeft.column);
        mapLayer.floodFill(assetLibrary, this.selectedTileset, mapCoordinate, tilesetCoordinate);
        this.mapUpdated.set(true);
    }

    public void eraseSelection(RowColumnPair position) {
        if (this.map == null || this.selectedTileset == null) return;
        if (this.selectedLayer < 0 || this.selectedLayer >= this.map.getLayers().size()) return;
        final RowColumnPair mapSize = this.map.getMapSize();
        if (position.row < 0 || position.row >= mapSize.row
                || position.column < 0 || position.column >= mapSize.column) {
            return;
        }
        int selectionWidth = this.selectionBottomRight.column - this.selectionTopLeft.column + 1;
        int selectionHeight = this.selectionBottomRight.row - this.selectionTopLeft.row + 1;
        if (position.column + selectionWidth > mapSize.column) {
            selectionWidth = mapSize.column - position.column;
        }
        if (position.row + selectionHeight > mapSize.row) {
            selectionHeight = mapSize.row - position.row;
        }
        final MapLayer mapLayer = this.map.getLayers().get(this.selectedLayer);
        for (int i = 0; i < selectionWidth; i++) {
            for (int j = 0; j < selectionHeight; j++) {
                final RowColumnPair mapCoordinate = RowColumnPair.of(position.row + j, position.column + i);
                mapLayer.erase(mapCoordinate);
                this.mapUpdated.set(true);
            }
        }
    }

    public void save() {
        if (this.map == null) return;
        final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle mapFile = projectFileHandle.sibling(String.format("maps/%s.json", this.map.id));
        mapFile.writeString(this.map.jsonString(4), false);
        this.mapUpdated.set(false);
    }

    public boolean addLayer() {
        if (this.map == null) return false;
        try {
            final TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("New Layer");
            textInputDialog.setHeaderText("Enter a new layer name");
            textInputDialog.initOwner(ApplicationContext.context().primaryStage);
            final Optional<String> optionalName = textInputDialog.showAndWait();
            if (optionalName.isEmpty()) return false;
            final String layerName = optionalName.get();
            if (layerName.isBlank()) {
                throw new IllegalArgumentException("Layer name cannot be empty");
            }
            this.map.addLayer(layerName);
            return true;
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, ApplicationContext.context().primaryStage);
        }
        return false;
    }

}
