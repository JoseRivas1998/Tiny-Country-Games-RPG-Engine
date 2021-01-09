package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class MusicAssetTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_PATH = "test_path.mp3";
    private static final float TEST_DURATION = 3.14f;

    @Test
    public void canCreateMusicAssetFromData() {
        MusicAsset musicAsset = MusicAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        assertNotNull(musicAsset);
        assertEquals(TEST_TITLE, musicAsset.title);
        assertEquals(TEST_PATH, musicAsset.path);
        assertEquals(TEST_DURATION, musicAsset.duration, 1e-5f);
    }

    @Test
    public void testToJSON() {
        final MusicAsset musicAsset = MusicAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        final JSONObject actualJSON = musicAsset.toJSON();
        this.assertJSONHasFields(actualJSON);
        this.assertJSONFieldsEqual(musicAsset, actualJSON);
    }

    private void assertJSONFieldsEqual(MusicAsset musicAsset, JSONObject actualJSON) {
        assertEquals(musicAsset.id.toString(), actualJSON.getString("id"));
        assertEquals(TEST_TITLE, actualJSON.getString("title"));
        assertEquals(TEST_PATH, actualJSON.getString("path"));
        assertEquals(TEST_DURATION, actualJSON.getFloat("duration"), 1e-9f);
    }

    private void assertJSONHasFields(JSONObject actualJSON) {
        assertTrue(actualJSON.has("id"));
        assertTrue(actualJSON.has("title"));
        assertTrue(actualJSON.has("path"));
        assertTrue(actualJSON.has("duration"));
    }

    @Test
    public void createMusicAssetFromJSON() {
        final JSONObject expectedJSON = this.createTestAssetJSON();
        MusicAsset musicAsset = MusicAsset.createFromJSON(expectedJSON.toString());
        this.assertJSONFieldsEqual(musicAsset, musicAsset.toJSON());
    }

    @Test
    public void verifyHashCode() {
        final MusicAsset musicAsset = MusicAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        final int expectedHash = Objects.hash(musicAsset.id, TEST_TITLE, TEST_PATH, TEST_DURATION);
        final int actualHash = musicAsset.hashCode();
        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void verifyEquals() {
        final JSONObject testAssetJSON = this.createTestAssetJSON();
        final MusicAsset asset1 = MusicAsset.createFromJSON(testAssetJSON.toString());
        final MusicAsset asset2 = MusicAsset.createFromJSON(testAssetJSON.toString());

        final JSONObject differentTitleObject = new JSONObject(testAssetJSON.toString());
        differentTitleObject.put("title", "");
        final MusicAsset differentTitle = MusicAsset.createFromJSON(differentTitleObject.toString());

        final JSONObject differentPathObject = new JSONObject(testAssetJSON.toString());
        differentPathObject.put("path", "");
        final MusicAsset differentPath = MusicAsset.createFromJSON(differentPathObject.toString());

        final JSONObject differentDurationObject = new JSONObject(testAssetJSON.toString());
        differentDurationObject.put("duration", 0f);
        final MusicAsset differentDuration = MusicAsset.createFromJSON(differentDurationObject.toString());

        final MusicAsset completelyDifferent = MusicAsset.createFromJSON(this.createTestAssetJSON().toString());

        assertEquals(asset1, asset1);
        assertEquals(asset2, asset2);

        assertEquals(asset1, asset2);
        assertEquals(asset2, asset1);

        this.assertNotEqualReflective(asset1, differentTitle);
        this.assertNotEqualReflective(asset1, differentPath);
        this.assertNotEqualReflective(asset1, differentDuration);
        this.assertNotEqualReflective(asset1, completelyDifferent);

        assertFalse(asset1.equals(null));
        assertFalse(asset1.equals("Some String"));

    }

    private void assertNotEqualReflective(MusicAsset asset1, MusicAsset asset2) {
        assertNotEquals(asset1, asset2);
        assertNotEquals(asset2, asset1);
    }

    private JSONObject createTestAssetJSON() {
        final JSONObject expectedJSON = new JSONObject();
        expectedJSON.put("id", UUID.randomUUID().toString());
        expectedJSON.put("title", TEST_TITLE);
        expectedJSON.put("path", TEST_PATH);
        expectedJSON.put("duration", TEST_DURATION);
        return expectedJSON;
    }

}
