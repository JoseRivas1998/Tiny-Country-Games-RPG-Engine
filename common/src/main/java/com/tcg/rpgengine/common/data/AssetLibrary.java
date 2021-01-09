package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class AssetLibrary implements JSONDocument{

    private static final String JSON_MUSIC_FIELD = "music";
    private final Map<UUID, SoundAsset> music;
    private final Map<UUID, Integer> assetReferenceCount;

    private AssetLibrary() {
        this.music = new HashMap<>();
        this.assetReferenceCount = new HashMap<>();
    }

    public static AssetLibrary newAssetLibrary() {
        return new AssetLibrary();
    }

    public static AssetLibrary fromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(jsonString));
        final JSONArray music = jsonObject.getJSONArray(JSON_MUSIC_FIELD);
        final AssetLibrary assetLibrary = new AssetLibrary();
        for (int i = 0; i < music.length(); i++) {
            final SoundAsset soundAsset = SoundAsset.createFromJSON(music.getJSONObject(i).toString());
            assetLibrary.music.put(soundAsset.id, soundAsset);
        }
        return assetLibrary;
    }

    public SoundAsset getMusicAssetById(UUID musicId) {
        return this.music.get(Objects.requireNonNull(musicId));
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
        if (this.getReferenceCount(soundAsset) > 0) {
            throw new IllegalStateException("This music asset is referenced once or more. It cannot be deleted.");
        }
        this.music.remove(soundAsset.id);
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

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_MUSIC_FIELD, this.assetCollectionToJSONArray(this.music.values()));

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
