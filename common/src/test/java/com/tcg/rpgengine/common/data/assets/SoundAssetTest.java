package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class SoundAssetTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_PATH = "test_path.mp3";
    private static final float TEST_DURATION = 3.14f;

    @Test
    public void canCreateMusicAssetFromData() {
        SoundAsset soundAsset = SoundAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        assertNotNull(soundAsset);
        assertEquals(TEST_TITLE, soundAsset.title);
        assertEquals(TEST_PATH, soundAsset.path);
        assertEquals(TEST_DURATION, soundAsset.duration, 1e-5f);
    }

    @Test
    public void testToJSON() {
        final SoundAsset soundAsset = SoundAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        final JSONObject actualJSON = soundAsset.toJSON();
        this.assertJSONHasFields(actualJSON);
        this.assertJSONFieldsEqual(soundAsset, actualJSON);
    }

    private void assertJSONFieldsEqual(SoundAsset soundAsset, JSONObject actualJSON) {
        assertEquals(soundAsset.id.toString(), actualJSON.getString("id"));
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
        SoundAsset soundAsset = SoundAsset.createFromJSON(expectedJSON.toString());
        this.assertJSONFieldsEqual(soundAsset, soundAsset.toJSON());
    }

    @Test
    public void verifyHashCode() {
        final SoundAsset soundAsset = SoundAsset.generateNewMusicAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        final int expectedHash = Objects.hash(soundAsset.id, TEST_TITLE, TEST_PATH, TEST_DURATION);
        final int actualHash = soundAsset.hashCode();
        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void verifyEquals() {
        final JSONObject testAssetJSON = this.createTestAssetJSON();
        final SoundAsset asset1 = SoundAsset.createFromJSON(testAssetJSON.toString());
        final SoundAsset asset2 = SoundAsset.createFromJSON(testAssetJSON.toString());

        final JSONObject differentTitleObject = new JSONObject(testAssetJSON.toString());
        differentTitleObject.put("title", "");
        final SoundAsset differentTitle = SoundAsset.createFromJSON(differentTitleObject.toString());

        final JSONObject differentPathObject = new JSONObject(testAssetJSON.toString());
        differentPathObject.put("path", "");
        final SoundAsset differentPath = SoundAsset.createFromJSON(differentPathObject.toString());

        final JSONObject differentDurationObject = new JSONObject(testAssetJSON.toString());
        differentDurationObject.put("duration", 0f);
        final SoundAsset differentDuration = SoundAsset.createFromJSON(differentDurationObject.toString());

        final SoundAsset completelyDifferent = SoundAsset.createFromJSON(this.createTestAssetJSON().toString());

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

    private void assertNotEqualReflective(SoundAsset asset1, SoundAsset asset2) {
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
