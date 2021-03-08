package com.tcg.rpgengine.common.data.misc;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.utils.UuidUtils;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiFunction;

public class AssetTable<T extends Asset> implements BinaryDocument {

    private final BiFunction<AssetLibrary, UUID, T> assetSupplier;
    private final List<UUID> assets;
    private final HashMap<UUID, Integer> indexLookupMap;

    private AssetTable(BiFunction<AssetLibrary, UUID, T> assetSupplier, Collection<UUID> assetIds) {
        this.assetSupplier = Objects.requireNonNull(assetSupplier);
        this.assets = new ArrayList<>();
        this.indexLookupMap = new HashMap<>();
        for (UUID assetId : assetIds) {
            this.insert(assetId);
        }
    }

    public static <T extends Asset> AssetTable<T> emptyAssetTable(BiFunction<AssetLibrary, UUID, T> assetSupplier) {
        return new AssetTable<>(assetSupplier, Collections.emptyList());
    }

    public static <T extends Asset> AssetTable<T> fromBytes(BiFunction<AssetLibrary, UUID, T> assetSupplier,
                                                            ByteBuffer bytes) {
        final List<UUID> assetIds = new ArrayList<>();
        final int listSize = bytes.getInt();
        for (int i = 0; i < listSize; i++) {
            assetIds.add(BinaryDocument.getUuid(bytes));
        }
        return new AssetTable<>(assetSupplier, assetIds);
    }

    public int insert(UUID uuid) {
        Objects.requireNonNull(uuid);
        final Optional<Integer> optionalIndex = Optional.ofNullable(this.indexLookupMap.get(uuid));
        if (optionalIndex.isPresent()) return optionalIndex.get();
        final int insertIndex = this.size();
        this.assets.add(uuid);
        this.indexLookupMap.put(uuid, insertIndex);
        return insertIndex;
    }

    public T get(AssetLibrary assetLibrary, int index) {
        return this.assetSupplier.apply(assetLibrary, this.assets.get(index));
    }

    public int size() {
        return this.assets.size();
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.putInt(this.size());
        for (UUID asset : this.assets) {
            bytes.put(UuidUtils.toBytes(asset));
        }
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return Integer.BYTES + (this.assets.size() * UuidUtils.UUID_NUMBER_OF_BYTES);
    }
}
