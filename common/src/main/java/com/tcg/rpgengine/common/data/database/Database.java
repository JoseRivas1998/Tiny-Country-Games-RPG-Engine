package com.tcg.rpgengine.common.data.database;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.database.entitycollections.Actors;
import com.tcg.rpgengine.common.data.database.entitycollections.Elements;

public class Database {

    public final Elements elements;
    public final Actors actors;

    public Database(AssetLibrary assetLibrary) {
        this.elements = new Elements(assetLibrary);
        this.actors = new Actors(assetLibrary);
    }
}
