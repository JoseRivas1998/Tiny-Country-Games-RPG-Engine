package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.MusicAsset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class AssetLibrary implements JSONDocument{

    private static final String JSON_MUSIC_FIELD = "music";
    private final Map<UUID, MusicAsset> music;

    private AssetLibrary() {
        this.music = new HashMap<>();
    }

    public static AssetLibrary newAssetLibrary() {
        return new AssetLibrary();
    }

    public static AssetLibrary fromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(Objects.requireNonNull(jsonString));
        final JSONArray music = jsonObject.getJSONArray(JSON_MUSIC_FIELD);
        final AssetLibrary assetLibrary = new AssetLibrary();
        for (int i = 0; i < music.length(); i++) {
            final MusicAsset musicAsset = MusicAsset.createFromJSON(music.getJSONObject(i).toString());
            assetLibrary.music.put(musicAsset.id, musicAsset);
        }
        return assetLibrary;
    }

    public MusicAsset getMusicAssetById(UUID musicId) {
        return this.music.get(Objects.requireNonNull(musicId));
    }

    public List<MusicAsset> getAllMusicAssets() {
        return new ArrayList<>(this.music.values());
    }

    public List<MusicAsset> getAllMusicAssetsSorted(Comparator<MusicAsset> musicAssetComparator) {
        return this.music.values()
                .stream()
                .sorted(musicAssetComparator)
                .collect(Collectors.toList());
    }

    public void addMusicAsset(MusicAsset musicAsset) {
        Objects.requireNonNull(musicAsset);
        this.music.put(musicAsset.id, musicAsset);
    }

    public void deleteMusicAsset(MusicAsset musicAsset) {
        this.music.remove(musicAsset.id);
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
