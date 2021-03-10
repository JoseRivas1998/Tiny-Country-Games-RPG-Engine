package com.tcg.rpgengine.common.data.database;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.database.entitycollections.Elements;

public class Database {

    public final Elements elements;

    public Database(AssetLibrary assetLibrary) {
        this.elements = new Elements(assetLibrary);
    }
}
