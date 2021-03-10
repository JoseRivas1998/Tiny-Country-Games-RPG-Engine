package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public abstract class Entity implements JSONDocument {

    public final static int HEADER_NUMBER_OF_BYTES = UuidUtils.UUID_NUMBER_OF_BYTES;

    protected static final String JSON_ID_FIELD = "id";
    public final UUID id;

    public Entity(UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    protected abstract void addAdditionalJSONData(JSONObject jsonObject);

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID_FIELD, this.id.toString());
        this.addAdditionalJSONData(jsonObject);
        return jsonObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || obj.getClass() != this.getClass()) {
                result = false;
            } else {
                final Entity other = (Entity) obj;
                result = this.id.equals(other.id);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }
}
