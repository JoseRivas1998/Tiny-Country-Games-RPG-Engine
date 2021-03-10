package com.tcg.rpgengine.common.data.misc;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;

public class RowColumnPair implements JSONDocument, BinaryDocument {

    private static final String JSON_COLUMN_FIELD = "column";
    private static final String JSON_ROW_FIELD = "row";
    public int row;
    public int column;

    private RowColumnPair(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static RowColumnPair of(int row, int column) {
        return new RowColumnPair(row, column);
    }

    public static RowColumnPair ofJSON(String json) {
        final JSONObject jsonObject = new JSONObject(json);
        final int row = jsonObject.getInt(JSON_ROW_FIELD);
        final int column = jsonObject.getInt(JSON_COLUMN_FIELD);
        return new RowColumnPair(row, column);
    }

    public static RowColumnPair ofBytes(ByteBuffer bytes) {
        final int row = bytes.getInt();
        final int column = bytes.getInt();
        return new RowColumnPair(row, column);
    }

    public RowColumnPair copy() {
        return new RowColumnPair(this.row, this.column);
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.putInt(this.row);
        bytes.putInt(this.column);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return Integer.BYTES * 2;
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ROW_FIELD, this.row);
        jsonObject.put(JSON_COLUMN_FIELD, this.column);
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RowColumnPair that = (RowColumnPair) o;
        return this.row == that.row && this.column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.row, this.column);
    }

    @Override
    public String toString() {
        return this.jsonString();
    }
}
