package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.UUID;

public class WindowSkin implements JSONDocument, BinaryDocument {

    private static final String JSON_IMAGE_ID_FIELD = "image_id";
    private UUID imageId;

    private WindowSkin(AssetLibrary assetLibrary, UUID imageId) {
        final ImageAsset windowSkinAsset = assetLibrary.getImageAssetById(imageId);
        assetLibrary.incrementReferenceCount(windowSkinAsset);
        this.imageId = windowSkinAsset.id;
    }

    public static WindowSkin createWindowSkin(AssetLibrary assetLibrary, UUID windowSkinImageId) {
        return new WindowSkin(assetLibrary, windowSkinImageId);
    }

    public static WindowSkin createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject json = new JSONObject(jsonString);
        final UUID imageId = UuidUtils.fromString(json.getString(JSON_IMAGE_ID_FIELD));
        return new WindowSkin(assetLibrary, imageId);
    }

    public static WindowSkin createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final UUID imageId = BinaryDocument.getUuid(bytes);
        return new WindowSkin(assetLibrary, imageId);
    }

    public void updateImage(AssetLibrary assetLibrary, UUID imageId) {
        if (!this.imageId.equals(imageId)) {
            final ImageAsset originalAsset = assetLibrary.getImageAssetById(this.imageId);
            final ImageAsset newAsset = assetLibrary.getImageAssetById(imageId);
            assetLibrary.decrementReferenceCount(originalAsset);
            assetLibrary.incrementReferenceCount(newAsset);
            this.imageId = newAsset.id;
        }
    }

    public UUID getWindowSkinId() {
        return this.imageId;
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        json.put(JSON_IMAGE_ID_FIELD, this.imageId.toString());
        return json;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        bytes.put(UuidUtils.toBytes(this.imageId));
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES;
    }
}
