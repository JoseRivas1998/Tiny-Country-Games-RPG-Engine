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
    private static final String JSON_GLOBAL_FLAGS_FIELD = "global_flags";
    public final Title title;
    public final UISounds uiSounds;
    public final WindowSkin windowSkin;
    private final Map<UUID, GlobalVariable> globalVariables;
    private final Map<UUID, Integer> globalVariableReferenceCount;

    private final Map<UUID, GlobalFlag> globalFlags;
    private final Map<UUID, Integer> globalFlagReferenceCount;

    private SystemData(Title title,
                       UISounds uiSounds,
                       WindowSkin windowSkin,
                       Collection<GlobalVariable> globalVariables,
                       Collection<GlobalFlag> globalFlags) {
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
        this.globalFlags = new HashMap<>();
        this.globalFlagReferenceCount = new HashMap<>();
        for (GlobalFlag globalFlag : globalFlags) {
            this.globalFlags.put(globalFlag.id, globalFlag);
            this.globalFlagReferenceCount.put(globalFlag.id, 0);
        }
    }

    public static SystemData createNewSystemData(Title title, UISounds uiSounds, WindowSkin windowSkin) {
        return new SystemData(title, uiSounds, windowSkin, Collections.emptyList(), Collections.emptyList());
    }

    public static SystemData createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final Title title = Title.createFromJSON(assetLibrary, jsonObject.getJSONObject(JSON_TITLE_FIELD).toString());
        final String uiSoundsJSON = jsonObject.getJSONObject(JSON_UI_SOUNDS_FIELD).toString();
        final UISounds uiSounds = UISounds.createFromJSON(assetLibrary, uiSoundsJSON);
        final String windowSkinJSON = jsonObject.getJSONObject(JSON_WINDOW_SKIN_FIELD).toString();
        final WindowSkin windowSkin = WindowSkin.createFromJSON(assetLibrary, windowSkinJSON);
        final List<GlobalVariable> globalVariables = SystemData.createGlobalVariableListFromJSON(jsonObject);
        final List<GlobalFlag> globalFlags = SystemData.createGlobalFlagsFromJSON(jsonObject);
        return new SystemData(title, uiSounds, windowSkin, globalVariables, globalFlags);
    }

    private static List<GlobalFlag> createGlobalFlagsFromJSON(JSONObject jsonObject) {
        final List<GlobalFlag> globalFlags = new ArrayList<>();
        final JSONArray globalFlagsJSONArray = jsonObject.getJSONArray(JSON_GLOBAL_FLAGS_FIELD);
        for (int i = 0; i < globalFlagsJSONArray.length(); i++) {
            final String globalFlagString = globalFlagsJSONArray.getJSONObject(i).toString();
            globalFlags.add(GlobalFlag.createFromJSON(globalFlagString));
        }
        return globalFlags;
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
        final List<GlobalFlag> globalFlags = createGlobalFlagsFromBytes(bytes);
        return new SystemData(title, uiSounds, windowSkin, globalVariables, globalFlags);
    }

    private static List<GlobalFlag> createGlobalFlagsFromBytes(ByteBuffer bytes) {
        final List<GlobalFlag> globalFlags = new ArrayList<>();
        final int globalFlagsLength = bytes.getInt();
        for (int i = 0; i < globalFlagsLength; i++) {
            globalFlags.add(GlobalFlag.createFromBytes(bytes));
        }
        return globalFlags;
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

    public void addGlobalFlag(GlobalFlag globalFlag) {
        Objects.requireNonNull(globalFlag);
        if (!this.globalFlags.containsKey(globalFlag.id)) {
            this.globalFlags.put(globalFlag.id, globalFlag);
            this.globalFlagReferenceCount.put(globalFlag.id, 0);
        }
    }

    public void incrementGlobalVariableReferenceCount(UUID id) {
        final int refCount = this.getGlobalVariableReferenceCount(id);
        this.globalVariableReferenceCount.put(id, refCount + 1);
    }

    public void incrementGlobalFlagReferenceCount(UUID id) {
        final int refCount = this.getGlobalFlagReferenceCount(id);
        this.globalFlagReferenceCount.put(id, refCount + 1);
    }

    public void decrementGlobalVariableReferenceCount(UUID id) {
        final int refCount = this.getGlobalVariableReferenceCount(id);
        this.globalVariableReferenceCount.put(id, Math.max(refCount - 1, 0));
    }

    public void decrementGlobalFlagReferenceCount(UUID id) {
        final int refCount = this.getGlobalFlagReferenceCount(id);
        this.globalFlagReferenceCount.put(id, Math.max(refCount - 1, 0));
    }

    public GlobalVariable getGlobalVariable(UUID id) {
        return Objects.requireNonNull(this.globalVariables.get(Objects.requireNonNull(id)));
    }

    public GlobalFlag getGlobalFlag(UUID id) {
        return Objects.requireNonNull(this.globalFlags.get(Objects.requireNonNull(id)));
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

    public void removeGlobalFlag(UUID id) {
        Objects.requireNonNull(id);
        if (this.globalFlags.containsKey(id)) {
            if (this.getGlobalFlagReferenceCount(id) != 0) {
                throw new IllegalStateException("There are still reference to that global flag.");
            }
            this.globalFlags.remove(id);
            this.globalFlagReferenceCount.remove(id);
        }
    }

    private int getGlobalVariableReferenceCount(UUID id) {
        return this.globalVariableReferenceCount.getOrDefault(id, 0);
    }

    private int getGlobalFlagReferenceCount(UUID id) {
        return this.globalFlagReferenceCount.getOrDefault(id, 0);
    }

    public List<GlobalVariable> getAllGlobalVariables() {
        return new ArrayList<>(this.globalVariables.values());
    }

    public List<GlobalFlag> getAllGlobalFlags() {
        return new ArrayList<>(this.globalFlags.values());
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title.toJSON());
        jsonObject.put(JSON_UI_SOUNDS_FIELD, this.uiSounds.toJSON());
        jsonObject.put(JSON_WINDOW_SKIN_FIELD, this.windowSkin.toJSON());
        jsonObject.put(JSON_GLOBAL_VARIABLES_FIELD, this.globalVariablesToJSONArray());
        jsonObject.put(JSON_GLOBAL_FLAGS_FIELD, this.globalFlagsToJSONArray());
        return jsonObject;
    }

    private JSONArray globalFlagsToJSONArray() {
        final JSONArray globalFlags = new JSONArray();
        this.globalFlags.values()
                .stream()
                .sorted(Comparator.comparing(globalFlag -> globalFlag.getName().toLowerCase()))
                .map(GlobalFlag::toJSON)
                .forEach(globalFlags::put);
        return globalFlags;
    }

    private JSONArray globalVariablesToJSONArray() {
        final JSONArray globalVariables = new JSONArray();
        this.globalVariables.values()
                .stream()
                .sorted(Comparator.comparing(globalVariable -> globalVariable.getName().toLowerCase()))
                .map(GlobalVariable::toJSON)
                .forEach(globalVariables::put);
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
        byteBuffer.putInt(this.globalFlags.size());
        for (GlobalFlag globalFlag : this.globalFlags.values()) {
            byteBuffer.put(globalFlag.toBytes());
        }
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return this.title.numberOfBytes()
                + this.uiSounds.numberOfBytes()
                + this.windowSkin.numberOfBytes()
                + this.calculateGlobalVariableByteCount()
                + this.calculateGlobalFlagByteCount();
    }

    private int calculateGlobalFlagByteCount() {
        return this.globalFlags.values().stream().mapToInt(GlobalFlag::numberOfBytes).sum() + Integer.BYTES;
    }

    private int calculateGlobalVariableByteCount() {
        return this.globalVariables.values().stream().mapToInt(GlobalVariable::numberOfBytes).sum() + Integer.BYTES;
    }
}
