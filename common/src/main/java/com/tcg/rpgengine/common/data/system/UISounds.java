package com.tcg.rpgengine.common.data.system;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.JSONDocument;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UISounds implements JSONDocument, BinaryDocument {

    private static final String JSON_CURSOR_FIELD = "cursor";
    private static final String JSON_OK_FIELD = "ok";
    private static final String JSON_CANCEL_FIELD = "cancel";
    private static final String JSON_BUZZER_FIELD = "buzzer";

    private UUID cursorId;
    private UUID okId;
    private UUID cancelId;
    private UUID buzzerId;

    private UISounds(AssetLibrary assetLibrary, UUID cursorId, UUID okId, UUID cancelId, UUID buzzerId) {
        this.cursorId = this.getSoundAssetAndIncrementReferenceCount(assetLibrary, cursorId);
        this.okId = this.getSoundAssetAndIncrementReferenceCount(assetLibrary, okId);
        this.cancelId = this.getSoundAssetAndIncrementReferenceCount(assetLibrary, cancelId);
        this.buzzerId = this.getSoundAssetAndIncrementReferenceCount(assetLibrary, buzzerId);
    }

    public static UISounds createNewUISounds(AssetLibrary assetLibrary, UUID cursorId, UUID okId, UUID cancelId,
                                             UUID buzzerId) {
        return new UISounds(assetLibrary, cursorId, okId, cancelId, buzzerId);
    }

    public static UISounds createFromJSON(AssetLibrary assetLibrary, String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        final UUID cursorId = UuidUtils.fromString(json.getString(JSON_CURSOR_FIELD));
        final UUID okId = UuidUtils.fromString(json.getString(JSON_OK_FIELD));
        final UUID cancelId = UuidUtils.fromString(json.getString(JSON_CANCEL_FIELD));
        final UUID buzzerId = UuidUtils.fromString(json.getString(JSON_BUZZER_FIELD));
        return new UISounds(assetLibrary, cursorId, okId, cancelId, buzzerId);
    }

    public static UISounds createFromBytes(AssetLibrary assetLibrary, ByteBuffer bytes) {
        final UUID cursorId = BinaryDocument.getUuid(bytes);
        final UUID okId = BinaryDocument.getUuid(bytes);
        final UUID cancelId = BinaryDocument.getUuid(bytes);
        final UUID buzzerId = BinaryDocument.getUuid(bytes);
        return new UISounds(assetLibrary, cursorId, okId, cancelId, buzzerId);
    }

    private UUID getSoundAssetAndIncrementReferenceCount(AssetLibrary assetLibrary, UUID cursorId) {
        final SoundAsset asset = assetLibrary.getSoundEffectAssetBytId(cursorId);
        assetLibrary.incrementReferenceCount(asset);
        return asset.id;
    }

    public void updateCursor(AssetLibrary assetLibrary, UUID soundEffectId) {
        if (!this.cursorId.equals(soundEffectId)) {
            this.cursorId = this.swapSoundAssets(assetLibrary, this.cursorId, soundEffectId).id;
        }
    }

    public void updateOk(AssetLibrary assetLibrary, UUID soundEffectId) {
        if (!this.okId.equals(soundEffectId)) {
            this.okId = this.swapSoundAssets(assetLibrary, this.okId, soundEffectId).id;
        }
    }

    public void updateCancel(AssetLibrary assetLibrary, UUID soundEffectId) {
        if (!this.cancelId.equals(soundEffectId)) {
            this.cancelId = this.swapSoundAssets(assetLibrary, this.cancelId, soundEffectId).id;
        }
    }

    public void updateBuzzer(AssetLibrary assetLibrary, UUID soundEffectId) {
        if (!this.buzzerId.equals(soundEffectId)) {
            this.buzzerId = this.swapSoundAssets(assetLibrary, this.buzzerId, soundEffectId).id;
        }
    }

    private SoundAsset swapSoundAssets(AssetLibrary assetLibrary, UUID originalId, UUID newId) {
        final SoundAsset originalAsset = assetLibrary.getSoundEffectAssetBytId(originalId);
        final SoundAsset newAsset = assetLibrary.getSoundEffectAssetBytId(newId);
        assetLibrary.decrementReferenceCount(originalAsset);
        assetLibrary.incrementReferenceCount(newAsset);
        return newAsset;
    }

    public UUID getCursorId() {
        return this.cursorId;
    }

    public UUID getOkId() {
        return this.okId;
    }

    public UUID getCancelId() {
        return this.cancelId;
    }

    public UUID getBuzzerId() {
        return this.buzzerId;
    }

    @Override
    public byte[] toBytes() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[this.numberOfBytes()]);
        byteBuffer.put(UuidUtils.toBytes(this.cursorId));
        byteBuffer.put(UuidUtils.toBytes(this.okId));
        byteBuffer.put(UuidUtils.toBytes(this.cancelId));
        byteBuffer.put(UuidUtils.toBytes(this.buzzerId));
        return byteBuffer.array();
    }

    @Override
    public int numberOfBytes() {
        return UuidUtils.UUID_NUMBER_OF_BYTES * 4;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(JSON_CURSOR_FIELD, this.cursorId.toString());
        json.put(JSON_OK_FIELD, this.okId.toString());
        json.put(JSON_CANCEL_FIELD, this.cancelId.toString());
        json.put(JSON_BUZZER_FIELD, this.buzzerId.toString());
        return json;
    }
}
