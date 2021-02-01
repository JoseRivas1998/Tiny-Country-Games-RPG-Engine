package com.tcg.rpgengine.common.data.misc;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONObject;

import java.nio.ByteBuffer;

public class Float2 implements JSONDocument, BinaryDocument {

    private static final String JSON_X_FIELD = "x";
    private static final String JSON_Y_FIELD = "y";
    public float x;
    public float y;

    private Float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Float2 of(float x, float y) {
        return new Float2(x, y);
    }

    public static Float2 zero() {
        return new Float2(0, 0);
    }

    public static Float2 fromJSON(String jsonString) {
        final JSONObject json = new JSONObject(jsonString);
        final float x = json.getFloat(JSON_X_FIELD);
        final float y = json.getFloat(JSON_Y_FIELD);
        return new Float2(x, y);
    }

    public static Float2 fromBytes(ByteBuffer bytes) {
        final float x = bytes.getFloat();
        final float y = bytes.getFloat();
        return new Float2(x, y);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put(JSON_X_FIELD, this.x);
        json.put(JSON_Y_FIELD, this.y);
        return json;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.putFloat(this.x);
        bytes.putFloat(this.y);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return Float.BYTES * 2;
    }
}
