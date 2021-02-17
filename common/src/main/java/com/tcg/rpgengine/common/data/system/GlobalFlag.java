package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class GlobalFlag implements JSONDocument, BinaryDocument {

    private static final String JSON_ID_FIELD = "id";
    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_INITIAL_VALUE_FIELD = "initial_value";
    public final UUID id;
    private String name;
    public boolean initialValue;

    public GlobalFlag(UUID id, String name, boolean initialValue) {
        this.id = id;
        this.setName(name);
        this.initialValue = initialValue;
    }

    public static GlobalFlag createNewGlobalFlag(String name, boolean initialValue) {
        return new GlobalFlag(UuidUtils.generateUuid(), name, initialValue);
    }

    public static GlobalFlag createFromJSON(String jsonString) {
        final JSONObject json = new JSONObject(jsonString);
        final UUID id = UuidUtils.fromString(json.getString(JSON_ID_FIELD));
        final String name = json.getString(JSON_NAME_FIELD);
        final boolean initialValue = json.getBoolean(JSON_INITIAL_VALUE_FIELD);
        return new GlobalFlag(id, name, initialValue);
    }

    public static GlobalFlag createFromBytes(ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final boolean initialValue = bytes.get() != 0;
        return new GlobalFlag(id, "", initialValue);
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

    @Override
    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES + Byte.BYTES;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.put(UuidUtils.toBytes(this.id));
        // When reading, it will be 0 for false, and nonzero for true, so just write all ones on true
        bytes.put(this.initialValue ? (byte) 0xFF : 0x00);
        return bytes.array();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || this.getClass() != obj.getClass()) {
                result = false;
            } else {
                GlobalFlag other = (GlobalFlag) obj;
                result = this.initialValue == other.initialValue
                        && this.id.equals(other.id)
                        && this.name.equals(other.name);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.initialValue);
    }
}
