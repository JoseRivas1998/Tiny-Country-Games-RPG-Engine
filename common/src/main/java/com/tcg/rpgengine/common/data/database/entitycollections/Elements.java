package com.tcg.rpgengine.common.data.database.entitycollections;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.entities.Element;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Stream;

public class Elements extends DatabaseEntityCollection<Element>{

    private final AssetLibrary assetLibrary;

    public Elements(AssetLibrary assetLibrary) {
        super();
        this.assetLibrary = assetLibrary;
    }

    public void loadFromJSON(String json) {
        final JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            this.add(Element.fromJSON(this.assetLibrary, jsonArray.get(i).toString()));
        }
    }

    public void loadFromBytes(ByteBuffer bytes) {
        final AssetTable<TiledImageAsset> assetTable = AssetTable.fromBytes(AssetLibrary::getIconPageById, bytes);
        while (bytes.hasRemaining()) {
            this.add(Element.fromBytes(this.assetLibrary, assetTable, bytes));
        }
    }

    @Override
    public byte[] toBytes() {
        final List<Element> elements = this.getAll();
        final AssetTable<TiledImageAsset> assetTable = AssetTable.emptyAssetTable(AssetLibrary::getIconPageById);
        final byte[][] elementBytes = new byte[elements.size()][];
        int totalElementBytes = 0;
        // doing the sum here too so we dont have to call the numberOfBytes function.
        for (int i = 0; i < elements.size(); i++) {
            elementBytes[i] = elements.get(i).toBytes(assetTable);
            totalElementBytes += elementBytes[i].length;
        }
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[assetTable.numberOfBytes() + totalElementBytes]);
        bytes.put(assetTable.toBytes());
        Stream.of(elementBytes).forEach(bytes::put);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        // oof
        final List<Element> elements = this.getAll();
        final Set<UUID> uniqueIconPages = new HashSet<>();
        int elementBytesSum = 0;
        for (Element element : elements) {
            elementBytesSum += element.numberOfBytes();
            uniqueIconPages.add(element.getIconPageId());
        }
        return (Integer.BYTES + uniqueIconPages.size() * UuidUtils.UUID_NUMBER_OF_BYTES) + elementBytesSum;
    }

    @Override
    protected void removeReferencesFromAssetLibrary(AssetLibrary assetLibrary, Element entity) {
        final TiledImageAsset iconPage = assetLibrary.getIconPageById(entity.getIconPageId());
        assetLibrary.decrementReferenceCount(iconPage);
    }
}
