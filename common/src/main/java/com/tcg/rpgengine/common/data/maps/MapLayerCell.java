package com.tcg.rpgengine.common.data.maps;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class MapLayerCell implements JSONDocument {

    private static final String JSON_TILESET_ID_FIELD = "tileset_id";
    private static final String JSON_MAP_COORDINATE_FIELD = "map_coordinate";
    private static final String JSON_TILESET_COORDINATE_FIELD = "tileset_coordinate";
    private UUID tilesetId;
    private final RowColumnPair mapCoordinate;
    private final RowColumnPair tilesetCoordinate;

    public MapLayerCell(AssetLibrary assetLibrary, UUID tilesetId,
                        RowColumnPair mapCoordinate, RowColumnPair tilesetCoordinate) {
        final TiledImageAsset tilesetAsset = assetLibrary.getTilesetAssetById(tilesetId);
        this.tilesetId = tilesetAsset.id;
        this.mapCoordinate = Objects.requireNonNull(mapCoordinate).copy();
        this.tilesetCoordinate = Objects.requireNonNull(tilesetCoordinate).copy();
    }

    public static MapLayerCell createNewCell(AssetLibrary assetLibrary, UUID tilesetId,
                                             RowColumnPair mapCoordinate, RowColumnPair tilesetCoordinate) {
        return new MapLayerCell(assetLibrary, tilesetId, mapCoordinate, tilesetCoordinate);
    }

    public static MapLayerCell ofJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final UUID tilesetId = UuidUtils.fromString(jsonObject.getString(JSON_TILESET_ID_FIELD));
        final JSONObject mapCoordinateJson = jsonObject.getJSONObject(JSON_MAP_COORDINATE_FIELD);
        final RowColumnPair mapCoordinate = RowColumnPair.ofJSON(mapCoordinateJson.toString());
        final JSONObject tilesetCoordinateJson = jsonObject.getJSONObject(JSON_TILESET_COORDINATE_FIELD);
        final RowColumnPair tilesetCoordinate = RowColumnPair.ofJSON(tilesetCoordinateJson.toString());
        return new MapLayerCell(assetLibrary, tilesetId, mapCoordinate, tilesetCoordinate);
    }

    public static MapLayerCell ofBytes(AssetLibrary assetLibrary, AssetTable<TiledImageAsset> assetTable,
                                       ByteBuffer bytes) {
        final TiledImageAsset tiledImageAsset = assetTable.get(assetLibrary, bytes.getInt());
        final RowColumnPair mapCoordinate = RowColumnPair.ofBytes(bytes);
        final RowColumnPair tilesetCoordinate = RowColumnPair.ofBytes(bytes);
        return new MapLayerCell(assetLibrary, tiledImageAsset.id, mapCoordinate, tilesetCoordinate);
    }

    public UUID getTilesetId() {
        return this.tilesetId;
    }

    public RowColumnPair getMapCoordinate() {
        return this.mapCoordinate.copy();
    }

    public RowColumnPair getTilesetCoordinate() {
        return this.tilesetCoordinate.copy();
    }

    public void updateMapCoordinate(RowColumnPair mapCoordinate) {
        Objects.requireNonNull(mapCoordinate);
        this.mapCoordinate.row = mapCoordinate.row;
        this.mapCoordinate.column = mapCoordinate.column;
    }

    public void updateTile(AssetLibrary assetLibrary, UUID tilesetId, RowColumnPair tilesetCoordinate) {
        if (!Objects.requireNonNull(tilesetId).equals(this.tilesetId)) {
            final TiledImageAsset tilesetAsset = assetLibrary.getTilesetAssetById(tilesetId);
            this.tilesetId = tilesetAsset.id;
        }
        Objects.requireNonNull(tilesetCoordinate);
        this.tilesetCoordinate.row = tilesetCoordinate.row;
        this.tilesetCoordinate.column = tilesetCoordinate.column;
    }

    public int getMapCoordinateAs1D(int mapWidth) {
        return this.mapCoordinate.row * mapWidth + this.mapCoordinate.column;
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put(JSON_TILESET_ID_FIELD, this.tilesetId.toString());
        json.put(JSON_MAP_COORDINATE_FIELD, this.mapCoordinate.toJSON());
        json.put(JSON_TILESET_COORDINATE_FIELD, this.tilesetCoordinate.toJSON());
        return json;
    }

    public int numberOfBytes() {
        return Integer.BYTES + this.mapCoordinate.numberOfBytes() + this.tilesetCoordinate.numberOfBytes();
    }

    public byte[] toBytes(AssetTable<TiledImageAsset> assetTable) {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        final int tilesetIndex = assetTable.insert(this.tilesetId);
        bytes.putInt(tilesetIndex);
        bytes.put(this.mapCoordinate.toBytes());
        bytes.put(this.tilesetCoordinate.toBytes());
        return bytes.array();
    }

}
