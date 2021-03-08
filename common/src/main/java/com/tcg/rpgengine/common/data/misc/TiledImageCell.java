package com.tcg.rpgengine.common.data.misc;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;

import java.util.Objects;
import java.util.UUID;

public abstract class TiledImageCell implements JSONDocument {

    private UUID tiledImageId;
    private int row;
    private int column;

    protected TiledImageCell(AssetLibrary assetLibrary, UUID tiledImageId, int row, int column) {
        final TiledImageAsset tiledImageAsset = this.getAssetFromAssetLibrary(Objects.requireNonNull(assetLibrary),
                Objects.requireNonNull(tiledImageId));
        assetLibrary.incrementReferenceCount(tiledImageAsset);
        this.tiledImageId = tiledImageAsset.id;

    }

    protected abstract TiledImageAsset getAssetFromAssetLibrary(AssetLibrary assetLibrary, UUID tiledImageId);

    private TiledImageAsset getTiledImageAsset(AssetLibrary assetLibrary) {
        return this.getAssetFromAssetLibrary(Objects.requireNonNull(assetLibrary), this.tiledImageId);
    }

    public void setId(AssetLibrary assetLibrary, UUID id) {
        Objects.requireNonNull(assetLibrary);
        if (!this.tiledImageId.equals(Objects.requireNonNull(id))) {
            final TiledImageAsset originalAsset = this.getAssetFromAssetLibrary(assetLibrary, this.tiledImageId);
            final TiledImageAsset newAsset = this.getAssetFromAssetLibrary(assetLibrary, id);
            assetLibrary.decrementReferenceCount(originalAsset);
            assetLibrary.incrementReferenceCount(newAsset);
            this.tiledImageId = newAsset.id;
        }
    }

    public void setRow(AssetLibrary assetLibrary, int row) {
        final TiledImageAsset tiledImageAsset = this.getTiledImageAsset(assetLibrary);
        if (row < 0 || row >= tiledImageAsset.rows) {
            throw new IllegalArgumentException("Row must be on interval [0, " + tiledImageAsset.rows + ").");
        }
        this.row = row;
    }

    public void setColumn(AssetLibrary assetLibrary, int column) {
        final TiledImageAsset tiledImageAsset = this.getTiledImageAsset(assetLibrary);
        if (column < 0 || column >= tiledImageAsset.columns) {
            throw new IllegalArgumentException("Column must be on on interval [0, " + tiledImageAsset.columns + ").");
        }
        this.column = column;
    }

    public UUID getTiledImageId() {
        return this.tiledImageId;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }
}
