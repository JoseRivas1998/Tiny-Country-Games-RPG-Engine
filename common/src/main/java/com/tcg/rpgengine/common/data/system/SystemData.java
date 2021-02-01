package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class SystemData implements JSONDocument, BinaryDocument {

    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_UI_SOUNDS_FIELD = "ui_sounds";
    private static final String JSON_WINDOW_SKIN_FIELD = "window_skin";
    public final Title title;
    public final UISounds uiSounds;
    public final WindowSkin windowSkin;

    private SystemData(Title title, UISounds uiSounds, WindowSkin windowSkin) {
        this.title = Objects.requireNonNull(title);
        this.uiSounds = Objects.requireNonNull(uiSounds);
        this.windowSkin = Objects.requireNonNull(windowSkin);
    }

    public static SystemData createNewSystemData(Title title, UISounds uiSounds, WindowSkin windowSkin) {
        return new SystemData(title, uiSounds, windowSkin);
    }

    public static SystemData createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final Title title = Title.createFromJSON(assetLibrary, jsonObject.getJSONObject(JSON_TITLE_FIELD).toString());
        final String uiSoundsJSON = jsonObject.getJSONObject(JSON_UI_SOUNDS_FIELD).toString();
        final UISounds uiSounds = UISounds.createFromJSON(assetLibrary, uiSoundsJSON);
        final String windowSkinJSON = jsonObject.getJSONObject(JSON_WINDOW_SKIN_FIELD).toString();
        final WindowSkin windowSkin = WindowSkin.createFromJSON(assetLibrary, windowSkinJSON);
        return new SystemData(title, uiSounds, windowSkin);
    }

    public static SystemData createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final Title title = Title.createFromBytes(assetLibrary, bytes);
        final UISounds uiSounds = UISounds.createFromBytes(assetLibrary, bytes);
        final WindowSkin windowSkin = WindowSkin.createFromBytes(assetLibrary, bytes);
        return new SystemData(title, uiSounds, windowSkin);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title.toJSON());
        jsonObject.put(JSON_UI_SOUNDS_FIELD, this.uiSounds.toJSON());
        jsonObject.put(JSON_WINDOW_SKIN_FIELD, this.windowSkin.toJSON());
        return jsonObject;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        byteBuffer.put(this.title.toBytes());
        byteBuffer.put(this.uiSounds.toBytes());
        byteBuffer.put(this.windowSkin.toBytes());
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return this.title.numberOfBytes() + this.uiSounds.numberOfBytes() + this.windowSkin.numberOfBytes();
    }
}
