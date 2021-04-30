package com.tcg.rpgengine.common.data.maps;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class MapLayer implements JSONDocument {

    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_CELLS_FIELD = "cells";
    private String name;
    private final Map<Integer, MapLayerCell> cells;
    private final RowColumnPair mapSize;

    private MapLayer(String name, RowColumnPair mapSize) {
        this.setName(name);
        this.cells = new HashMap<>();
        this.mapSize = Objects.requireNonNull(mapSize).copy();
    }

    public static MapLayer createNewLayer(String name, RowColumnPair mapSize) {
        return new MapLayer(name, mapSize);
    }

    public static MapLayer fromJSON(AssetLibrary assetLibrary, RowColumnPair mapSize, String json) {
        final JSONObject jsonObject = new JSONObject(json);
        final String name = jsonObject.getString(JSON_NAME_FIELD);
        final MapLayer layer = new MapLayer(name, mapSize);

        final JSONArray cellsArray = jsonObject.getJSONArray(JSON_CELLS_FIELD);
        for (int i = 0; i < cellsArray.length(); i++) {
            final MapLayerCell cell = MapLayerCell.ofJSON(assetLibrary, cellsArray.getJSONObject(i).toString());
            final int index = cell.getMapCoordinateAs1D(layer.mapSize.column);
            layer.cells.put(index, cell);
        }

        return layer;
    }

    public static MapLayer fromBytes(AssetLibrary assetLibrary, AssetTable<TiledImageAsset> assetTable,
                                     RowColumnPair mapSize, ByteBuffer bytes) {
        final String name = BinaryDocument.getUTF8String(bytes);
        final MapLayer mapLayer = new MapLayer(name, mapSize);
        final int numCells = bytes.getInt();
        for (int i = 0; i < numCells; i++) {
            final MapLayerCell cell = MapLayerCell.ofBytes(assetLibrary, assetTable, bytes);
            final int index = cell.getMapCoordinateAs1D(mapLayer.mapSize.column);
            mapLayer.cells.put(index, cell);
        }
        return mapLayer;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void putCell(AssetLibrary assetLibrary, UUID tilesetId,
                        RowColumnPair mapCoordinate, RowColumnPair tilesetCoordinate) {
        Objects.requireNonNull(mapCoordinate);
        Objects.requireNonNull(tilesetCoordinate);
        final int index = this.mapCoordinateToIndex(mapCoordinate);
        if (this.cells.containsKey(index)) {
            final MapLayerCell mapLayerCell = this.cells.get(index);
            mapLayerCell.updateMapCoordinate(mapCoordinate);
            mapLayerCell.updateTile(assetLibrary, tilesetId, tilesetCoordinate);
        } else {
            final MapLayerCell newCell = MapLayerCell.createNewCell(assetLibrary, tilesetId,
                    mapCoordinate, tilesetCoordinate);
            this.cells.put(index, newCell);
        }
    }

    public void fillAllBlanks(AssetLibrary assetLibrary, UUID tilesetId, RowColumnPair tilesetCoordinate) {
        final RowColumnPair mapCoordinate = RowColumnPair.of(0, 0);
        for (mapCoordinate.row = 0; mapCoordinate.row < this.mapSize.row; mapCoordinate.row++) {
            for (mapCoordinate.column = 0; mapCoordinate.column < this.mapSize.column; mapCoordinate.column++) {
                final int index = this.mapCoordinateToIndex(mapCoordinate);
                if (!this.cells.containsKey(index)) {
                    final MapLayerCell cell = MapLayerCell.createNewCell(assetLibrary, tilesetId,
                            mapCoordinate, tilesetCoordinate);
                    this.cells.put(index, cell);
                }
            }
        }
    }

    public void erase(RowColumnPair mapCoordinate) {
        Objects.requireNonNull(mapCoordinate);
        final int index = this.mapCoordinateToIndex(mapCoordinate);
        this.cells.remove(index);
    }

    public void resize(RowColumnPair mapSize) {
        this.mapSize.row = mapSize.row;
        this.mapSize.column = mapSize.column;
        final List<MapLayerCell> cells = this.getCells();
        this.cells.clear();
        cells.stream()
                .filter(mapLayerCell -> mapLayerCell.getMapCoordinate().row < this.mapSize.row)
                .filter(mapLayerCell -> mapLayerCell.getMapCoordinate().column < this.mapSize.column)
                .forEach(mapLayerCell -> {
                    final int index = mapLayerCell.getMapCoordinateAs1D(this.mapSize.column);
                    this.cells.put(index, mapLayerCell);
                });
    }

    public void floodFill(AssetLibrary assetLibrary, UUID tilesetId,
                          RowColumnPair mapCoordinate, RowColumnPair tilesetCoordinate) {
        final int index = this.mapCoordinateToIndex(mapCoordinate);
        final RowColumnPair neighborCoordinate = RowColumnPair.of(0, 0);

        final int topNeighbor = Math.max(mapCoordinate.row - 1, 0);
        final int bottomNeighbor = Math.min(mapCoordinate.row + 1, this.mapSize.row - 1);
        final int leftNeighbor = Math.max(mapCoordinate.column - 1, 0);
        final int rightNeighbor = Math.min(mapCoordinate.column + 1, this.mapSize.column - 1);

        final boolean isEmpty = !this.cells.containsKey(index);
        final MapLayerCell cell = this.cells.get(index);

        // If the cell is the same type as flood fill, do nothing
        if (!isEmpty
                && cell.getTilesetId().equals(tilesetId)
                && cell.getTilesetCoordinate().row == tilesetCoordinate.row
                && cell.getTilesetCoordinate().column == tilesetCoordinate.column) {
            return;
        }

        // List to store values to recurse with
        List<RowColumnPair> cellsToRecurse = new ArrayList<>();

        // For each numerical neighbor:
        for (neighborCoordinate.row = topNeighbor;
             neighborCoordinate.row <= bottomNeighbor;
             neighborCoordinate.row++) {
            for (neighborCoordinate.column = leftNeighbor;
                 neighborCoordinate.column <= rightNeighbor;
                 neighborCoordinate.column++) {
                if (neighborCoordinate.row == mapCoordinate.row && neighborCoordinate.column == mapCoordinate.column) {
                    continue;
                }
                final int neighborIndex = this.mapCoordinateToIndex(neighborCoordinate);
                if (this.cells.containsKey(neighborIndex)) {
                    if (!isEmpty) {
                        final MapLayerCell neighborCell = this.cells.get(neighborIndex);
                        // If both exist, and their tile matches, then they match, recurse on that neighbor
                        if (cell.getTilesetId().equals(neighborCell.getTilesetId()) &&
                                cell.getTilesetCoordinate().row == neighborCell.getMapCoordinate().row &&
                                cell.getTilesetCoordinate().column == neighborCell.getMapCoordinate().column) {
                            cellsToRecurse.add(neighborCoordinate.copy());
                        }
                    }
                } else if (isEmpty) {
                    // If both dont exist, then they match, recurse on that neighbor
                    cellsToRecurse.add(neighborCoordinate.copy());
                }
            }
        }
        // Update original cell to avoid back tracking
        this.putCell(assetLibrary, tilesetId, mapCoordinate, tilesetCoordinate);

        // Recurse on matching neighbors
        for (RowColumnPair coordinate : cellsToRecurse) {
            this.floodFill(assetLibrary, tilesetId, coordinate, tilesetCoordinate);
        }
    }

    private int mapCoordinateToIndex(RowColumnPair mapCoordinate) {
        return mapCoordinate.row * this.mapSize.column + mapCoordinate.column;
    }

    public List<MapLayerCell> getCells() {
        return this.cells.values()
                .stream()
                .sorted(Comparator.comparing(mapLayerCell -> mapLayerCell.getMapCoordinateAs1D(this.mapSize.column)))
                .collect(Collectors.toList());
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put(JSON_NAME_FIELD, this.name);
        final JSONArray cellsArray = new JSONArray();
        this.getCells()
                .stream()
                .map(MapLayerCell::toJSON)
                .forEach(cellsArray::put);
        json.put(JSON_CELLS_FIELD, cellsArray);
        return json;
    }

    public int numberOfBytes() {
        final int numberOfBytesForCells = this.cells.values().stream().mapToInt(MapLayerCell::numberOfBytes).sum();
        return Integer.BYTES + this.name.length() + Integer.BYTES + numberOfBytesForCells;
    }

    public byte[] toBytes(AssetTable<TiledImageAsset> assetTable) {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        BinaryDocument.putUTF8String(bytes, this.name);
        bytes.putInt(this.cells.size());
        this.cells.values()
                .stream()
                .sorted(Comparator.comparing(mapLayerCell -> mapLayerCell.getMapCoordinateAs1D(this.mapSize.column)))
                .map(mapLayerCell -> mapLayerCell.toBytes(assetTable))
                .forEach(bytes::put);
        return bytes.array();
    }

}
