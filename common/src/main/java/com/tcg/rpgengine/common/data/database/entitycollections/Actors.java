package com.tcg.rpgengine.common.data.database.entitycollections;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.entities.Actor;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class Actors extends DatabaseEntityCollection<Actor> {

    private final AssetLibrary assetLibrary;

    public Actors(AssetLibrary assetLibrary) {
        super();
        this.assetLibrary = assetLibrary;
    }

    public void loadFromJSON(String json) {
        final JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            this.add(Actor.fromJSON(this.assetLibrary, jsonArray.get(i).toString()));
        }
    }

    public void loadFromBytes(ByteBuffer bytes) {
        final AssetTable<TiledImageAsset> assetTable = AssetTable.fromBytes(
                AssetLibrary::getSpritesheetPageAssetById, bytes
        );
        while (bytes.hasRemaining()) {
            this.add(Actor.fromBytes(this.assetLibrary, assetTable, bytes));
        }
    }

    @Override
    public void remove(AssetLibrary assetLibrary, Actor entity) {
        if(this.size() == 1) {
            throw new IllegalStateException("There is only one Actor left and there cannot be zero.");
        }
        super.remove(assetLibrary, entity);
    }

    @Override
    public byte[] toBytes() {
        final List<Actor> actors = this.getAll();
        final AssetTable<TiledImageAsset> assetTable = AssetTable.emptyAssetTable(
                AssetLibrary::getSpritesheetPageAssetById
        );
        final byte[][] actorBytes = new byte[actors.size()][];
        int totalActorBytes = 0;
        for (int i = 0; i < actors.size(); i++) {
            actorBytes[i] = actors.get(i).toBytes(assetTable);
            totalActorBytes += actorBytes[i].length;
        }
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[assetTable.numberOfBytes() + totalActorBytes]);
        bytes.put(assetTable.toBytes());
        Stream.of(actorBytes).forEach(bytes::put);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        final List<Actor> actors = this.getAll();
        final Set<UUID> uniqueSpritesheetPages = new HashSet<>();
        int actorByteSum = 0;
        for (Actor actor : actors) {
            actorByteSum += actor.numberOfBytes();
            uniqueSpritesheetPages.add(actor.getSpritesheetPageId());
        }
        return (Integer.BYTES + uniqueSpritesheetPages.size() * UuidUtils.UUID_NUMBER_OF_BYTES) + actorByteSum;
    }

    @Override
    protected void removeReferencesFromAssetLibrary(AssetLibrary assetLibrary, Actor entity) {
        final TiledImageAsset spritesheetPageAsset = assetLibrary.getSpritesheetPageAssetById(
                entity.getSpritesheetPageId()
        );
        assetLibrary.decrementReferenceCount(spritesheetPageAsset);
    }
}
