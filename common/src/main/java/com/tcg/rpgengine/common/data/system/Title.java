package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class Title implements JSONDocument, BinaryDocument {

    private static final String JSON_TITLE_FIELD = "title";
    private static final String JSON_IMAGE_FIELD = "image";
    private static final String JSON_MUSIC_FIELD = "music";

    public String title;
    private UUID imageId;
    private UUID musicId;

    private Title(AssetLibrary assetLibrary, String title, UUID imageId, UUID musicId) {
        this.title = Objects.requireNonNull(title);

        final ImageAsset imageAsset = assetLibrary.getImageAssetById(imageId);
        this.imageId = imageAsset.id;
        assetLibrary.incrementReferenceCount(imageAsset);

        final SoundAsset musicAsset = assetLibrary.getMusicAssetById(musicId);
        this.musicId = musicAsset.id;
        assetLibrary.incrementReferenceCount(musicAsset);
    }

    public static Title createNewTitle(AssetLibrary assetLibrary, String title, UUID imageId, UUID musicId) {
        return new Title(assetLibrary, title, imageId, musicId);
    }

    public static Title createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final String title = jsonObject.getString(JSON_TITLE_FIELD);
        final UUID imageId = UuidUtils.fromString(jsonObject.getString(JSON_IMAGE_FIELD));
        final UUID musicId = UuidUtils.fromString(jsonObject.getString(JSON_MUSIC_FIELD));
        return new Title(assetLibrary, title, imageId, musicId);
    }

    public static Title createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final String title = BinaryDocument.getUTF8String(bytes);
        final UUID imageId = BinaryDocument.getUuid(bytes);
        final UUID musicId = BinaryDocument.getUuid(bytes);
        return new Title(assetLibrary, title, imageId, musicId);
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

    public void updateMusic(AssetLibrary assetLibrary, UUID musicId) {
        if (!this.musicId.equals(musicId)) {
            final SoundAsset originalAsset = assetLibrary.getMusicAssetById(this.musicId);
            final SoundAsset newAsset = assetLibrary.getMusicAssetById(musicId);
            assetLibrary.decrementReferenceCount(originalAsset);
            assetLibrary.incrementReferenceCount(newAsset);

            this.musicId = newAsset.id;
        }
    }

    public UUID getImageId() {
        return this.imageId;
    }

    public UUID getMusicId() {
        return this.musicId;
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE_FIELD, this.title);
        jsonObject.put(JSON_IMAGE_FIELD, this.imageId.toString());
        jsonObject.put(JSON_MUSIC_FIELD, this.musicId.toString());
        return jsonObject;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        BinaryDocument.putUTF8String(byteBuffer, this.title);
        byteBuffer.put(UuidUtils.toBytes(this.imageId));
        byteBuffer.put(UuidUtils.toBytes(this.musicId));
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return Integer.BYTES + this.title.length() + UuidUtils.UUID_NUMBER_OF_BYTES + UuidUtils.UUID_NUMBER_OF_BYTES;
    }
}
