package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class GlobalVariable implements JSONDocument, BinaryDocument {

    private static final String JSON_ID_FIELD = "id";
    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_INITIAL_VALUE_FIELD = "initial_value";
    public final UUID id;
    private String name;
    public float initialValue;

    public GlobalVariable(UUID id, String name, float initialValue) {
        this.id = Objects.requireNonNull(id);
        this.setName(name);
        this.initialValue = initialValue;
    }

    public static GlobalVariable createNewGlobalVariable(String name, float initialValue) {
        return new GlobalVariable(UuidUtils.generateUuid(), name, initialValue);
    }

    public static GlobalVariable createFromJSON(String jsonString) {
        final JSONObject json = new JSONObject(jsonString);
        final UUID id = UuidUtils.fromString(json.getString(JSON_ID_FIELD));
        final String name = json.getString(JSON_NAME_FIELD);
        final float initialValue = json.getFloat(JSON_INITIAL_VALUE_FIELD);
        return new GlobalVariable(id, name, initialValue);
    }

    // Here, we always use a blank name since the game runtime does not need to know the name of a variable, only
    // the editor so the developer can keep track. This is because the variables will be referenced by id, not by name.
    public static GlobalVariable createFromBytes(ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final float initialValue = bytes.getFloat();
        return new GlobalVariable(id, "", initialValue);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put(JSON_ID_FIELD, this.id.toString());
        json.put(JSON_NAME_FIELD, this.name);
        json.put(JSON_INITIAL_VALUE_FIELD, this.initialValue);
        return json;
    }

    // The binary version of this is only used by the runtime, so saving the name is unnecessary so it will be
    // omitted from the binary version to save space
    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.put(UuidUtils.toBytes(this.id));
        bytes.putFloat(this.initialValue);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES + Float.BYTES;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.initialValue, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || obj.getClass() != this.getClass()) {
                result = false;
            } else {
                final GlobalVariable other = (GlobalVariable) obj;
                result = this.id.equals(other.id)
                        && this.name.equals(other.name)
                        && Float.compare(this.initialValue, other.initialValue) == 0;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.jsonString();
    }
}
