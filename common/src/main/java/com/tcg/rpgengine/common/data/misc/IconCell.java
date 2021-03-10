package com.tcg.rpgengine.common.data.misc;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IconCell extends TiledImageCell {

    protected IconCell(AssetLibrary assetLibrary, UUID tiledImageId, int row, int column) {
        super(assetLibrary, tiledImageId, row, column);
    }

    public static IconCell createNewIconCell(AssetLibrary assetLibrary, UUID tiledImageId, int row, int column) {
        return new IconCell(assetLibrary, tiledImageId, row, column);
    }

    public static IconCell createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final UUID imageId = UuidUtils.fromString(jsonObject.getString(JSON_IMAGE_ID_FIELD));
        final int row = jsonObject.getInt(JSON_ROW_FIELD);
        final int column = jsonObject.getInt(JSON_COLUMN_FIELD);
        return new IconCell(assetLibrary, imageId, row, column);
    }

    public static IconCell createFromBytes(AssetTable<TiledImageAsset> assetTable, AssetLibrary assetLibrary,
                                           ByteBuffer bytes) {
        final int idIndex = bytes.getInt();
        final int row = bytes.getInt();
        final int column = bytes.getInt();
        final TiledImageAsset iconPageAsset = assetTable.get(assetLibrary, idIndex);
        return new IconCell(assetLibrary, iconPageAsset.id, row, column);
    }

    @Override
    protected TiledImageAsset getAssetFromAssetLibrary(AssetLibrary assetLibrary, UUID tiledImageId) {
        return assetLibrary.getIconPageById(tiledImageId);
    }

}
