package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.*;

public class SystemData implements JSONDocument, BinaryDocument {

    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_UI_SOUNDS_FIELD = "ui_sounds";
    private static final String JSON_WINDOW_SKIN_FIELD = "window_skin";
    private static final String JSON_GLOBAL_VARIABLES_FIELD = "global_variables";
    public final Title title;
    public final UISounds uiSounds;
    public final WindowSkin windowSkin;
    private final Map<UUID, GlobalVariable> globalVariables;
    private final Map<UUID, Integer> globalVariableReferenceCount;

    private SystemData(Title title,
                       UISounds uiSounds,
                       WindowSkin windowSkin,
                       Collection<GlobalVariable> globalVariables) {
        this.title = Objects.requireNonNull(title);
        this.uiSounds = Objects.requireNonNull(uiSounds);
        this.windowSkin = Objects.requireNonNull(windowSkin);
        Objects.requireNonNull(globalVariables);
        this.globalVariables = new HashMap<>();
        this.globalVariableReferenceCount = new HashMap<>();
        for (GlobalVariable globalVariable : globalVariables) {
            this.globalVariables.put(globalVariable.id, globalVariable);
            this.globalVariableReferenceCount.put(globalVariable.id, 0);
        }
    }

    public static SystemData createNewSystemData(Title title, UISounds uiSounds, WindowSkin windowSkin) {
        return new SystemData(title, uiSounds, windowSkin, Collections.emptyList());
    }

    public static SystemData createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final Title title = Title.createFromJSON(assetLibrary, jsonObject.getJSONObject(JSON_TITLE_FIELD).toString());
        final String uiSoundsJSON = jsonObject.getJSONObject(JSON_UI_SOUNDS_FIELD).toString();
        final UISounds uiSounds = UISounds.createFromJSON(assetLibrary, uiSoundsJSON);
        final String windowSkinJSON = jsonObject.getJSONObject(JSON_WINDOW_SKIN_FIELD).toString();
        final WindowSkin windowSkin = WindowSkin.createFromJSON(assetLibrary, windowSkinJSON);
        final List<GlobalVariable> globalVariables = SystemData.createGlobalVariableListFromJSON(jsonObject);
        return new SystemData(title, uiSounds, windowSkin, globalVariables);
    }

    private static List<GlobalVariable> createGlobalVariableListFromJSON(JSONObject jsonObject) {
        final List<GlobalVariable> globalVariables = new ArrayList<>();
        final JSONArray globalVariablesJSONArray = jsonObject.getJSONArray(JSON_GLOBAL_VARIABLES_FIELD);
        for (int i = 0; i < globalVariablesJSONArray.length(); i++) {
            final String globalVariableString = globalVariablesJSONArray.getJSONObject(i).toString();
            globalVariables.add(GlobalVariable.createFromJSON(globalVariableString));
        }
        return globalVariables;
    }

    public static SystemData createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final Title title = Title.createFromBytes(assetLibrary, bytes);
        final UISounds uiSounds = UISounds.createFromBytes(assetLibrary, bytes);
        final WindowSkin windowSkin = WindowSkin.createFromBytes(assetLibrary, bytes);
        final List<GlobalVariable> globalVariables = SystemData.createGlobalVariablesFromBytes(bytes);
        return new SystemData(title, uiSounds, windowSkin, globalVariables);
    }

    private static List<GlobalVariable> createGlobalVariablesFromBytes(ByteBuffer bytes) {
        final List<GlobalVariable> globalVariables = new ArrayList<>();
        final int globalVariablesLength = bytes.getInt();
        for (int i = 0; i < globalVariablesLength; i++) {
            globalVariables.add(GlobalVariable.createFromBytes(bytes));
        }
        return globalVariables;
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        Objects.requireNonNull(globalVariable);
        if (!this.globalVariables.containsKey(globalVariable.id)) {
            this.globalVariables.put(globalVariable.id, globalVariable);
            this.globalVariableReferenceCount.put(globalVariable.id, 0);
        }
    }

    public void incrementGlobalVariableReferenceCount(UUID id) {
        final int refCount = this.getGlobalVariableReferenceCount(id);
        this.globalVariableReferenceCount.put(id, refCount + 1);
    }

    public void decrementGlobalVariableReferenceCount(UUID id) {
        final int refCount = this.getGlobalVariableReferenceCount(id);
        this.globalVariableReferenceCount.put(id, Math.max(refCount - 1, 0));
    }

    public GlobalVariable getGlobalVariable(UUID id) {
        return Objects.requireNonNull(this.globalVariables.get(Objects.requireNonNull(id)));
    }

    public void removeGlobalVariable(UUID id) {
        Objects.requireNonNull(id);
        if (this.globalVariables.containsKey(id)) {
            if(this.getGlobalVariableReferenceCount(id) != 0) {
                throw new IllegalStateException("There are still references to that global variable.");
            }
            this.globalVariables.remove(id);
            this.globalVariableReferenceCount.remove(id);
        }
    }

    private int getGlobalVariableReferenceCount(UUID id) {
        return this.globalVariableReferenceCount.getOrDefault(id, 0);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title.toJSON());
        jsonObject.put(JSON_UI_SOUNDS_FIELD, this.uiSounds.toJSON());
        jsonObject.put(JSON_WINDOW_SKIN_FIELD, this.windowSkin.toJSON());
        jsonObject.put(JSON_GLOBAL_VARIABLES_FIELD, this.globalVariablesToJSONArray());
        return jsonObject;
    }

    private JSONArray globalVariablesToJSONArray() {
        final JSONArray globalVariables = new JSONArray();
        for (GlobalVariable globalVariable : this.globalVariables.values()) {
            globalVariables.put(globalVariable.toJSON());
        }
        return globalVariables;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        byteBuffer.put(this.title.toBytes());
        byteBuffer.put(this.uiSounds.toBytes());
        byteBuffer.put(this.windowSkin.toBytes());
        byteBuffer.putInt(this.globalVariables.size());
        for (GlobalVariable globalVariable : this.globalVariables.values()) {
            byteBuffer.put(globalVariable.toBytes());
        }
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return this.title.numberOfBytes()
                + this.uiSounds.numberOfBytes()
                + this.windowSkin.numberOfBytes()
                + this.calculateGlobalVariableByteCount();
    }

    private int calculateGlobalVariableByteCount() {
        return this.globalVariables.values().stream().mapToInt(GlobalVariable::numberOfBytes).sum() + Integer.BYTES;
    }
}
