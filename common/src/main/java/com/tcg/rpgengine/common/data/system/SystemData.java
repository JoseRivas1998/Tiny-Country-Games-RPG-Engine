package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.JSONDocument;
import org.json.JSONObject;

public class SystemData implements JSONDocument {

    private static final String JSON_TITLE_FIELD = "title";
    public final Title title;

    private SystemData(Title title) {
        this.title = title;
    }

    public static SystemData createNewSystemData(Title title) {
        return new SystemData(title);
    }

    public static SystemData createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final Title title = Title.createFromJSON(assetLibrary, jsonObject.getJSONObject(JSON_TITLE_FIELD).toString());
        return new SystemData(title);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title.toJSON());
        return jsonObject;
    }
}
