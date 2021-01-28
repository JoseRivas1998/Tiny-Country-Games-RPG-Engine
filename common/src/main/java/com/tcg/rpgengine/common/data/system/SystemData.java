package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;

public class SystemData implements JSONDocument, BinaryDocument {

    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_UI_SOUNDS_FIELD = "ui_sounds";
    public final Title title;
    public final UISounds uiSounds;

    private SystemData(Title title, UISounds uiSounds) {
        this.title = Objects.requireNonNull(title);
        this.uiSounds = Objects.requireNonNull(uiSounds);
    }

    public static SystemData createNewSystemData(Title title, UISounds uiSounds) {
        return new SystemData(title, uiSounds);
    }

    public static SystemData createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final Title title = Title.createFromJSON(assetLibrary, jsonObject.getJSONObject(JSON_TITLE_FIELD).toString());
        final String uiSoundsJSON = jsonObject.getJSONObject(JSON_UI_SOUNDS_FIELD).toString();
        final UISounds uiSounds = UISounds.createFromJSON(assetLibrary, uiSoundsJSON);
        return new SystemData(title, uiSounds);
    }

    public static SystemData createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final Title title = Title.createFromBytes(assetLibrary, bytes);
        final UISounds uiSounds = UISounds.createFromBytes(assetLibrary, bytes);
        return new SystemData(title, uiSounds);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title.toJSON());
        jsonObject.put(JSON_UI_SOUNDS_FIELD, this.uiSounds.toJSON());
        return jsonObject;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        byteBuffer.put(this.title.toBytes());
        byteBuffer.put(this.uiSounds.toBytes());
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return this.title.numberOfBytes() + this.uiSounds.numberOfBytes();
    }
}
