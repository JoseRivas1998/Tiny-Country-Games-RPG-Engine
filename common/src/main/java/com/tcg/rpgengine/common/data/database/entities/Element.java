package com.tcg.rpgengine.common.data.database.entities;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.IconCell;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class Element extends Entity implements JSONDocument {

    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_ICON_FIELD = "icon";
    private String name;
    private final IconCell iconCell;

    private Element(UUID id, String name, IconCell iconCell) {
        super(id);
        this.setName(name);
        this.iconCell = iconCell;
    }

    public static Element createNewElement(String name, IconCell iconCell) {
        return new Element(UuidUtils.generateUuid(), name, iconCell);
    }

    public static Element fromJSON(AssetLibrary assetLibrary, String json) {
        final JSONObject jsonObject = new JSONObject(json);
        final UUID id = UuidUtils.fromString(jsonObject.getString(Entity.JSON_ID_FIELD));
        final String name = jsonObject.getString(JSON_NAME_FIELD);
        final JSONObject iconCellJSON = jsonObject.getJSONObject(JSON_ICON_FIELD);
        final IconCell iconCell = IconCell.createFromJSON(assetLibrary, iconCellJSON.toString());
        return new Element(id, name, iconCell);
    }

    public static Element fromBytes(AssetLibrary assetLibrary, AssetTable<TiledImageAsset> assetTable,
                                    ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final String name = BinaryDocument.getUTF8String(bytes);
        final IconCell iconCell = IconCell.createFromBytes(assetTable, assetLibrary, bytes);
        return new Element(id, name, iconCell);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return this.name;
    }

    public void setIcon(AssetLibrary assetLibrary, UUID iconPageId, int row, int column) {
        this.iconCell.setId(assetLibrary, iconPageId);
        this.iconCell.setRow(assetLibrary, row);
        this.iconCell.setColumn(assetLibrary, column);
    }

    public UUID getIconPageId() {
        return this.iconCell.getTiledImageId();
    }

    public RowColumnPair getIconIndex() {
        return RowColumnPair.of(this.iconCell.getRow(), this.iconCell.getColumn());
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_NAME_FIELD, this.name);
        jsonObject.put(JSON_ICON_FIELD, this.iconCell.toJSON());
    }

    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES + Integer.BYTES + this.name.length() + this.iconCell.numberOfBytes();
    }

    public byte[] toBytes(AssetTable<TiledImageAsset> assetTable) {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.put(UuidUtils.toBytes(this.id));
        BinaryDocument.putUTF8String(bytes, this.name);
        bytes.put(this.iconCell.toBytes(assetTable));
        return bytes.array();
    }

}
