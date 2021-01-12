package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public final class SoundAsset extends Asset {

    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_PATH_FIELD = "path";
    private static final String JSON_DURATION_FIELD = "duration";

    public String title;
    public String path;
    public float duration;

    private SoundAsset(UUID id, String title, String path, float duration) {
        super(id);
        this.title = title;
        this.path = path;
        this.duration = duration;
    }

    public static SoundAsset generateNewSoundAsset(String title, String path, float duration) {
        return new SoundAsset(UuidUtils.generateUuid(), title, path, duration);
    }

    public static SoundAsset createFromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final UUID id = UuidUtils.fromString(jsonObject.getString(JSON_ID_FIELD));
        final String title = jsonObject.getString(JSON_TITLE_FIELD);
        final String path = jsonObject.getString(JSON_PATH_FIELD);
        final float duration = jsonObject.getFloat(JSON_DURATION_FIELD);
        return new SoundAsset(id, title, path, duration);
    }

    public static SoundAsset createFromBytes(ByteBuffer bytes) {
        final byte[] idBytes = new byte[UuidUtils.UUID_NUMBER_OF_BYTES];
        bytes.get(idBytes);
        final UUID id = UuidUtils.fromBytes(idBytes);

        final int titleLength = bytes.getInt();
        final byte[] titleBytes = new byte[titleLength];
        bytes.get(titleBytes);
        final String title = new String(titleBytes, StandardCharsets.UTF_8);

        final int pathLength = bytes.getInt();
        final byte[] pathBytes = new byte[pathLength];
        bytes.get(pathBytes);
        final String path = new String(pathBytes, StandardCharsets.UTF_8);

        final float duration = bytes.getFloat();
        return new SoundAsset(id, title, path, duration);
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_TITLE_FIELD, this.title);
        jsonObject.put(JSON_PATH_FIELD, this.path);
        jsonObject.put(JSON_DURATION_FIELD, this.duration);
    }

    @Override
    protected int contentLength() {
        return Integer.BYTES + this.title.length() + Integer.BYTES + this.path.length() + Float.BYTES;
    }

    @Override
    protected void encodeContent(ByteBuffer byteBuffer) {
        this.putString(byteBuffer, this.title);
        this.putString(byteBuffer, this.path);
        byteBuffer.putFloat(this.duration);
    }

    private void putString(ByteBuffer byteBuffer, String string) {
        final byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        byteBuffer.putInt(stringBytes.length);
        byteBuffer.put(stringBytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.title, this.path, this.duration);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || obj.getClass() != this.getClass()) {
                result = false;
            } else {
                final SoundAsset other = (SoundAsset) obj;
                return super.equals(other)
                        && this.title.equals(other.title)
                        && this.path.equals(other.path)
                        && Float.compare(this.duration, other.duration) == 0;
            }
        }
        return result;
    }
}
