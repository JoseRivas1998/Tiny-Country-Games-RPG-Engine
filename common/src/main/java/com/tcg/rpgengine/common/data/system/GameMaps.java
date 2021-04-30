package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONCollection;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.util.*;

public class GameMaps implements JSONCollection, BinaryDocument {

    private final Set<UUID> maps;

    private GameMaps(List<UUID> maps) {
        this.maps = new HashSet<>(maps);
    }

    public static GameMaps createNewMapList(List<UUID> maps) {
        return new GameMaps(maps);
    }

    public static GameMaps fromJSON(String json) {
        final JSONArray jsonArray = new JSONArray(json);
        final List<UUID> maps = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            maps.add(UuidUtils.fromString(jsonArray.getString(i)));
        }
        return new GameMaps(maps);
    }

    public static GameMaps fromBinary(ByteBuffer bytes) {
        final List<UUID> maps = new ArrayList<>();
        final int numMaps = bytes.getInt();
        for (int i = 0; i < numMaps; i++) {
            maps.add(BinaryDocument.getUuid(bytes));
        }
        return new GameMaps(maps);
    }

    public void addMap(UUID mapId) {
        this.maps.add(mapId);
    }

    public void removeMap(UUID mapId) {
        if (this.maps.size() == 1) {
            throw new IllegalStateException("There must be at least one map.");
        }
        this.maps.remove(mapId);
    }

    public List<UUID> getAllMaps() {
        return new ArrayList<>(this.maps);
    }


    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.putInt(this.maps.size());
        this.maps.stream()
                .map(UuidUtils::toBytes)
                .forEach(bytes::put);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return Integer.BYTES + (this.maps.size() * UuidUtils.UUID_NUMBER_OF_BYTES);
    }

    @Override
    public JSONArray toJSON() {
        final JSONArray jsonArray = new JSONArray();
        this.maps.stream()
                .map(UUID::toString)
                .forEach(jsonArray::put);
        return jsonArray;
    }
}
