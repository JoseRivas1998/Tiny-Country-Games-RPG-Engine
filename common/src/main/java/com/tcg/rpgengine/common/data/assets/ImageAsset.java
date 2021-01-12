package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class ImageAsset extends Asset {

    private static final String JSON_PATH_FIELD = "path";
    public String path;

    public ImageAsset(UUID id, String path) {
        super(id);
        this.path = Objects.requireNonNull(path);
    }

    public static ImageAsset generateNewImageAsset(String path) {
        return new ImageAsset(UuidUtils.generateUuid(), path);
    }

    public static ImageAsset createFromJSON(String jsonString) {
        final JSONObject imageJSON = new JSONObject(jsonString);
        final UUID id = UuidUtils.fromString(imageJSON.getString(JSON_ID_FIELD));
        final String path = imageJSON.getString(JSON_PATH_FIELD);
        return new ImageAsset(id, path);
    }

    public static ImageAsset createFromBytes(ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final String path = BinaryDocument.getUTF8String(bytes);
        return new ImageAsset(id, path);
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_PATH_FIELD, this.path);
    }

    @Override
    protected int contentLength() {
        return Integer.BYTES + this.path.length();
    }

    @Override
    protected void encodeContent(ByteBuffer byteBuffer) {
        BinaryDocument.putUTF8String(byteBuffer, this.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.path);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || this.getClass() != obj.getClass()) {
                result = false;
            } else {
                final ImageAsset other = (ImageAsset) obj;
                result = super.equals(other) && this.path.equals(other.path);
            }
        }
        return result;
    }
}
