package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

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

    public static SoundAsset generateNewMusicAsset(String title, String path, float duration) {
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

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_TITLE_FIELD, this.title);
        jsonObject.put(JSON_PATH_FIELD, this.path);
        jsonObject.put(JSON_DURATION_FIELD, this.duration);
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
