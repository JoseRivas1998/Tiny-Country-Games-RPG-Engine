package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.assets.SpritesheetPageAsset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class AssetLibrary implements JSONDocument {

    private static final String JSON_MUSIC_FIELD = "music";
    private static final String JSON_IMAGES_FIELD = "images";
    private static final String JSON_SOUND_FIELD = "sound";
    private static final String JSON_SPRITESHEET_PAGES_FIELD = "spritesheet_pages";
    private final Map<UUID, SoundAsset> music;
    private final Map<UUID, ImageAsset> images;
    private final Map<UUID, SoundAsset> soundEffects;
    private final Map<UUID, SpritesheetPageAsset> spritesheetPages;

    private final Map<UUID, Integer> assetReferenceCount;

    private AssetLibrary() {
        this.music = new HashMap<>();
        this.images = new HashMap<>();
        this.soundEffects = new HashMap<>();
        this.assetReferenceCount = new HashMap<>();
        this.spritesheetPages = new HashMap<>();
    }

    public static AssetLibrary newAssetLibrary() {
        return new AssetLibrary();
    }

    public static AssetLibrary fromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(jsonString));
        final JSONArray music = jsonObject.getJSONArray(JSON_MUSIC_FIELD);
        final JSONArray images = jsonObject.getJSONArray(JSON_IMAGES_FIELD);
        final JSONArray sound = jsonObject.getJSONArray(JSON_SOUND_FIELD);
        final JSONArray spritesheetPages = jsonObject.getJSONArray(JSON_SPRITESHEET_PAGES_FIELD);
        final AssetLibrary assetLibrary = new AssetLibrary();
        for (int i = 0; i < music.length(); i++) {
            final SoundAsset soundAsset = SoundAsset.createFromJSON(music.getJSONObject(i).toString());
            assetLibrary.music.put(soundAsset.id, soundAsset);
        }
        for (int i = 0; i < images.length(); i++) {
            final ImageAsset imageAsset = ImageAsset.createFromJSON(images.getJSONObject(i).toString());
            assetLibrary.images.put(imageAsset.id, imageAsset);
        }
        for (int i = 0; i < sound.length(); i++) {
            final SoundAsset soundAsset = SoundAsset.createFromJSON(sound.getJSONObject(i).toString());
            assetLibrary.soundEffects.put(soundAsset.id, soundAsset);
        }
        for (int i = 0; i < spritesheetPages.length(); i++) {
            final String spritesheetJson = spritesheetPages.getJSONObject(i).toString();
            final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createFromJSON(spritesheetJson);
            assetLibrary.spritesheetPages.put(spritesheetPageAsset.id, spritesheetPageAsset);
        }
        return assetLibrary;
    }

    public SoundAsset getMusicAssetById(UUID musicId) {
        return getNonNullAsset(musicId, this.music);
    }

    public List<SoundAsset> getAllMusicAssets() {
        return new ArrayList<>(this.music.values());
    }

    public List<SoundAsset> getAllMusicAssetsSorted(Comparator<SoundAsset> musicAssetComparator) {
        return this.music.values()
                .stream()
                .sorted(musicAssetComparator)
                .collect(Collectors.toList());
    }

    public void addMusicAsset(SoundAsset soundAsset) {
        Objects.requireNonNull(soundAsset);
        this.music.put(soundAsset.id, soundAsset);
    }

    public void deleteMusicAsset(SoundAsset soundAsset) {
        this.verifyReferenceCount(soundAsset);
        this.music.remove(soundAsset.id);
    }

    public byte[] musicAssetBytes() {
        return encodeAssetCollection(this.music.values());
    }

    public ImageAsset getImageAssetById(UUID imageId) {
        return getNonNullAsset(imageId, this.images);
    }

    public List<ImageAsset> getAllImageAssets() {
        return new ArrayList<>(this.images.values());
    }

    public void addImageAsset(ImageAsset asset) {
        this.images.put(asset.id, asset);
    }

    public void deleteImageAsset(ImageAsset asset) {
        this.verifyReferenceCount(asset);
        this.images.remove(asset.id);
    }

    public byte[] imageAssetBytes() {
        return encodeAssetCollection(this.images.values());
    }

    public SoundAsset getSoundEffectAssetBytId(UUID soundEffectId) {
        return getNonNullAsset(soundEffectId, this.soundEffects);
    }

    public List<SoundAsset> getAllSoundEffectAssets() {
        return new ArrayList<>(this.soundEffects.values());
    }

    public List<SoundAsset> getAllSoundAssetsSorted(Comparator<SoundAsset> soundAssetComparator) {
        return this.soundEffects.values()
                .stream()
                .sorted(soundAssetComparator)
                .collect(Collectors.toList());
    }

    public void addSoundEffectAsset(SoundAsset soundAsset) {
        Objects.requireNonNull(soundAsset);
        this.soundEffects.put(soundAsset.id, soundAsset);
    }

    public void deleteSoundEffectAsset(SoundAsset soundAsset) {
        this.verifyReferenceCount(soundAsset);
        this.soundEffects.remove(soundAsset.id);
    }

    public byte[] soundAssetBytes() {
        return encodeAssetCollection(this.soundEffects.values());
    }

    public SpritesheetPageAsset getSpritesheetPageAssetById(UUID spritesheetPageId) {
        return getNonNullAsset(spritesheetPageId, this.spritesheetPages);
    }

    public List<SpritesheetPageAsset> getAllSpritesheetPages() {
        return new ArrayList<>(this.spritesheetPages.values());
    }

    public void addSpritesheetPageAsset(SpritesheetPageAsset spritesheetPageAsset) {
        Objects.requireNonNull(spritesheetPageAsset);
        this.spritesheetPages.put(spritesheetPageAsset.id, spritesheetPageAsset);
    }

    public void deleteSpritesheetPageAsset(SpritesheetPageAsset spritesheetPageAsset) {
        this.verifyReferenceCount(spritesheetPageAsset);
        this.spritesheetPages.remove(spritesheetPageAsset.id);
    }

    public byte[] spritesheetPagesBytes() {
        return encodeAssetCollection(this.spritesheetPages.values());
    }

    private void verifyReferenceCount(Asset asset) {
        if (this.getReferenceCount(asset) > 0) {
            throw new IllegalStateException("This asset is referenced once or more. It cannot be deleted.");
        }
    }

    public void incrementReferenceCount(Asset asset) {
        this.assetReferenceCount.put(asset.id, this.getReferenceCount(asset) + 1);
    }

    public void decrementReferenceCount(Asset asset) {
        this.assetReferenceCount.put(asset.id, Math.max(0, this.getReferenceCount(asset) - 1));
    }

    public int getReferenceCount(Asset asset) {
        return this.assetReferenceCount.getOrDefault(asset.id, 0);
    }

    private static <T extends Asset> T getNonNullAsset(UUID assetId, Map<UUID, T> assetMap) {
        return Objects.requireNonNull(assetMap.get(Objects.requireNonNull(assetId)));
    }

    public static <T extends Asset> byte[] encodeAssetCollection(Collection<T> assets) {
        final int totalBytes = assets.stream()
                .mapToInt(Asset::numberOfBytes)
                .sum();
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[totalBytes]);
        assets.stream()
                .map(Asset::toBytes)
                .forEach(byteBuffer::put);
        return byteBuffer.array();
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_MUSIC_FIELD, this.assetCollectionToJSONArray(this.music.values()));
        jsonObject.put(JSON_IMAGES_FIELD, this.assetCollectionToJSONArray(this.images.values()));
        jsonObject.put(JSON_SOUND_FIELD, this.assetCollectionToJSONArray(this.soundEffects.values()));
        jsonObject.put(JSON_SPRITESHEET_PAGES_FIELD, this.assetCollectionToJSONArray(this.spritesheetPages.values()));

        return jsonObject;
    }

    private JSONArray assetCollectionToJSONArray(Collection<? extends Asset> assets) {
        final JSONArray jsonArray = new JSONArray();
        assets.stream()
                .map(Asset::toJSON)
                .forEach(jsonArray::put);
        return jsonArray;
    }

}
