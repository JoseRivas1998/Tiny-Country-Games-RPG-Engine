package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class SoundAssetTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_PATH = "test_path.mp3";
    private static final float TEST_DURATION = 3.14f;
    private static final byte[] TEST_BYTES = {
            // UUID (8ca4a250-9edc-40fc-9bc6-5e00938546ba)
            (byte) 0x8c, (byte) 0xa4, (byte) 0xa2, 0x50,
            (byte) 0x9e, (byte) 0xdc,
            0x40, (byte) 0xfc,
            (byte) 0x9b, (byte) 0xc6,
            0x5e, 0x00, (byte) 0x93, (byte) 0x85, 0x46, (byte) 0xba,
            // title length (10)
            0x00, 0x00, 0x00, 0x0a,
            // tile string
            0x54, 0x65, 0x73, 0x74, 0x20, 0x54, 0x69, 0x74, 0x6c, 0x65,
            // path length (13)
            0x00, 0x00, 0x00, 0x0d,
            // path string
            0x74, 0x65, 0x73, 0x74, 0x5f, 0x70, 0x61, 0x74, 0x68, 0x2e, 0x6d, 0x70, 0x33,
            // duration (3.14)
            0x40, 0x48, (byte) 0xf5, (byte) 0xc3
    };

    @Test
    public void canCreateMusicAssetFromData() {
        SoundAsset soundAsset = SoundAsset.generateNewSoundAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        assertNotNull(soundAsset);
        assertEquals(TEST_TITLE, soundAsset.title);
        assertEquals(TEST_PATH, soundAsset.path);
        assertEquals(TEST_DURATION, soundAsset.duration, 1e-5f);
    }

    @Test
    public void testToJSON() {
        final SoundAsset soundAsset = SoundAsset.generateNewSoundAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
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
        final SoundAsset soundAsset = SoundAsset.generateNewSoundAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
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

    @Test
    public void verifyContentLength() {
        final SoundAsset soundAsset = SoundAsset.generateNewSoundAsset(TEST_TITLE, TEST_PATH, TEST_DURATION);
        final int expected = 35;
        final int actual = soundAsset.contentLength();
        assertEquals(expected, actual);
    }

    @Test
    public void verifyBytes() {
        final UUID soundId = UUID.fromString("8ca4a250-9edc-40fc-9bc6-5e00938546ba");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", soundId.toString());
        jsonObject.put("title", TEST_TITLE);
        jsonObject.put("path", TEST_PATH);
        jsonObject.put("duration", TEST_DURATION);
        final SoundAsset soundAsset = SoundAsset.createFromJSON(jsonObject.toString());
        final byte[] actualBytes = soundAsset.toBytes();
        assertArrayEquals(TEST_BYTES, actualBytes);
    }

    @Test
    public void verifyDecodeBytes() {
        final UUID soundId = UUID.fromString("8ca4a250-9edc-40fc-9bc6-5e00938546ba");
        final SoundAsset soundAsset = SoundAsset.createFromBytes(ByteBuffer.wrap(TEST_BYTES));
        assertEquals(soundId, soundAsset.id);
        assertEquals(TEST_TITLE, soundAsset.title);
        assertEquals(TEST_PATH, soundAsset.path);
        assertEquals(TEST_DURATION, soundAsset.duration, 1e-5f);
    }

}
