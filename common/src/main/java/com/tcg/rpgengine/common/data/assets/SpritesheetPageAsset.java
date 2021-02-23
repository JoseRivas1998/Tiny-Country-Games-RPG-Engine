package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SpritesheetPageAsset extends Asset{



    public SpritesheetPageAsset(UUID id) {
        super(id);
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {

    }

    @Override
    protected int contentLength() {
        return 0;
    }

    @Override
    protected void encodeContent(ByteBuffer byteBuffer) {

    }
}
