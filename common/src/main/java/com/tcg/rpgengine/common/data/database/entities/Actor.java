package com.tcg.rpgengine.common.data.database.entities;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.common.data.misc.SpritesheetCharacter;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

// Right now this will look VERY similar to element, but after I finish the capstone I can add more to this class
public class Actor extends Entity {

    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_CHARACTER_FIELD = "character";
    private String name;
    private final SpritesheetCharacter character;

    public Actor(UUID id, String name, SpritesheetCharacter character) {
        super(id);
        this.setName(name);
        this.character = Objects.requireNonNull(character);
    }

    public static Actor createNewActor(String name, SpritesheetCharacter character) {
        return new Actor(UuidUtils.generateUuid(), name ,character);
    }

    public static Actor fromJSON(AssetLibrary assetLibrary, String json) {
        final JSONObject jsonObject = new JSONObject(json);
        final UUID id = UuidUtils.fromString(jsonObject.getString(Entity.JSON_ID_FIELD));
        final String name = jsonObject.getString(JSON_NAME_FIELD);
        final String characterJSON = jsonObject.getJSONObject(JSON_CHARACTER_FIELD).toString();
        final SpritesheetCharacter character = SpritesheetCharacter.createFromJSON(assetLibrary, characterJSON);
        return new Actor(id, name, character);
    }

    public static Actor fromBytes(AssetLibrary assetLibrary, AssetTable<TiledImageAsset> assetTable, ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final String name = BinaryDocument.getUTF8String(bytes);
        final SpritesheetCharacter character = SpritesheetCharacter.createFromBytes(assetTable, assetLibrary, bytes);
        return new Actor(id, name, character);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return this.name;
    }

    public void setCharacter(AssetLibrary assetLibrary, UUID spritesheetPageId, int row, int column) {
        this.character.setId(assetLibrary, spritesheetPageId);
        this.character.setRow(assetLibrary, row);
        this.character.setColumn(assetLibrary, column);
    }

    public UUID getSpritesheetPageId() {
        return this.character.getTiledImageId();
    }

    public RowColumnPair getCharacterIndex() {
        return RowColumnPair.of(this.character.getRow(), this.character.getColumn());
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_NAME_FIELD, this.name);
        jsonObject.put(JSON_CHARACTER_FIELD, this.character.toJSON());
    }

    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES + Integer.BYTES + this.name.length() + this.character.numberOfBytes();
    }

    public byte[] toBytes(AssetTable<TiledImageAsset> assetTable) {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.put(UuidUtils.toBytes(this.id));
        BinaryDocument.putUTF8String(bytes, this.name);
        bytes.put(this.character.toBytes(assetTable));
        return bytes.array();
    }

}
