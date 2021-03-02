package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class TiledImageAsset extends Asset {

    private static final String JSON_PATH_FIELD = "path";
    private static final String JSON_COLUMNS_FIELD = "columns";
    private static final String JSON_ROWS_FIELD = "rows";
    private String path;
    public int rows;
    public int columns;

    public TiledImageAsset(UUID id, String path, int rows, int columns) {
        super(id);
        this.setPath(path);
        this.rows = rows;
        this.columns = columns;
    }

    public static TiledImageAsset createNewTiledImageAsset(String path, int rows, int columns) {
        return new TiledImageAsset(UuidUtils.generateUuid(), path, rows, columns);
    }

    public static TiledImageAsset createFromJSON(String jsonString) {
        final JSONObject json = new JSONObject(jsonString);
        final UUID id = UuidUtils.fromString(json.getString(JSON_ID_FIELD));
        final String path = json.getString(JSON_PATH_FIELD);
        final int rows = json.getInt(JSON_ROWS_FIELD);
        final int columns = json.getInt(JSON_COLUMNS_FIELD);
        return new TiledImageAsset(id, path, rows, columns);
    }

    public static TiledImageAsset createFromBytes(ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final String path = BinaryDocument.getUTF8String(bytes);
        final int rows = bytes.getInt();
        final int columns = bytes.getInt();
        return new TiledImageAsset(id, path, rows, columns);
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_PATH_FIELD, this.path);
        jsonObject.put(JSON_ROWS_FIELD, this.rows);
        jsonObject.put(JSON_COLUMNS_FIELD, this.columns);
    }

    @Override
    protected int contentLength() {
        // three integers, one for the length of the string, one for the rows, and one for the columns
        // Strings are encoded in UTF-8, which is one byte for each character
        return 3 * Integer.BYTES + this.path.length();
    }

    @Override
    protected void encodeContent(ByteBuffer byteBuffer) {
        BinaryDocument.putUTF8String(byteBuffer, this.path);
        byteBuffer.putInt(this.rows);
        byteBuffer.putInt(this.columns);
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public boolean equals(Object o) {
        boolean result =this == o;
        if (!result) {
            if (o == null || this.getClass() != o.getClass()) {
                result = false;
            } else if (!super.equals(o)){
                result = false;
            } else {
                TiledImageAsset other = (TiledImageAsset) o;
                result = this.rows == other.rows && this.columns == other.columns && this.path.equals(other.path);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.path, this.rows, this.columns);
    }
}
