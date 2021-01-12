package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class AssetLibrary implements JSONDocument {

    private static final String JSON_MUSIC_FIELD = "music";
    private static final String JSON_IMAGES_FIELD = "images";
    private final Map<UUID, SoundAsset> music;
    private final Map<UUID, ImageAsset> images;

    private final Map<UUID, Integer> assetReferenceCount;

    private AssetLibrary() {
        this.music = new HashMap<>();
        this.images = new HashMap<>();
        this.assetReferenceCount = new HashMap<>();
    }

    public static AssetLibrary newAssetLibrary() {
        return new AssetLibrary();
    }

    public static AssetLibrary fromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(jsonString));
        final JSONArray music = jsonObject.getJSONArray(JSON_MUSIC_FIELD);
        final JSONArray images = jsonObject.getJSONArray(JSON_IMAGES_FIELD);
        final AssetLibrary assetLibrary = new AssetLibrary();
        for (int i = 0; i < music.length(); i++) {
            final SoundAsset soundAsset = SoundAsset.createFromJSON(music.getJSONObject(i).toString());
            assetLibrary.music.put(soundAsset.id, soundAsset);
        }
        for (int i = 0; i < images.length(); i++) {
            final ImageAsset imageAsset = ImageAsset.createFromJSON(images.getJSONObject(i).toString());
            assetLibrary.images.put(imageAsset.id, imageAsset);
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

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_MUSIC_FIELD, this.assetCollectionToJSONArray(this.music.values()));

        jsonObject.put(JSON_IMAGES_FIELD, this.assetCollectionToJSONArray(this.images.values()));

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
