package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public abstract class Asset extends Entity implements BinaryDocument {

    public Asset(UUID id) {
        super(id);
    }

    protected abstract void addAdditionalJSONData(JSONObject jsonObject);

    protected abstract int contentLength();
    protected abstract void encodeContent(ByteBuffer byteBuffer);

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID_FIELD, this.id.toString());
        this.addAdditionalJSONData(jsonObject);
        return jsonObject;
    }

    @Override
    public int numberOfBytes() {
        return Asset.HEADER_NUMBER_OF_BYTES + this.contentLength();
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        byteBuffer.put(UuidUtils.toBytes(this.id));
        this.encodeContent(byteBuffer);
        return byteBuffer.array();
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
                final Asset other = (Asset) obj;
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
