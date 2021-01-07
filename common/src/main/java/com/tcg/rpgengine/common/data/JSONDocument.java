package com.tcg.rpgengine.common.data;

import org.json.JSONObject;

public interface JSONDocument {

    JSONObject toJSON();

    default String jsonString() {
        return this.toJSON().toString();
    }

    default String jsonString(int indentFactor) {
        return this.toJSON().toString(indentFactor);
    }

}
