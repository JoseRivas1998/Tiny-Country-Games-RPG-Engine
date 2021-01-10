package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;
import static com.tcg.rpgengine.common.TestUtils.*;

public class ImageAssetTest {

    private static final String TEST_IMAGE_PATH = "test_image.png";

    @Test
    public void canCreateImageAsset() {
        ImageAsset imageAsset = ImageAsset.generateNewImageAsset(TEST_IMAGE_PATH);
        assertNotNull(imageAsset);
        assertEquals(TEST_IMAGE_PATH, imageAsset.path);
    }

    @Test
    public void verifyJSON() {
        final ImageAsset imageAsset = ImageAsset.generateNewImageAsset(TEST_IMAGE_PATH);
        final JSONObject expected = new JSONObject();
        expected.put("id", imageAsset.id.toString());
        expected.put("path", TEST_IMAGE_PATH);
        final JSONObject actual = imageAsset.toJSON();
        assertJSONObjectsEquals(expected, actual);
    }

    @Test
    public void testCreateCreateImageAssetFromJSON() {
        final UUID imageId = UUID.randomUUID();
        final JSONObject imageJSON = new JSONObject();
        imageJSON.put("id", imageId);
        imageJSON.put("path", TEST_IMAGE_PATH);
        final ImageAsset imageAsset = ImageAsset.createFromJSON(imageJSON.toString());
        assertNotNull(imageAsset);
        assertEquals(imageId, imageAsset.id);
        assertEquals(TEST_IMAGE_PATH, imageAsset.path);
    }

    @Test
    public void cannotCreateAssetWithNullPath() {
        assertThrows(NullPointerException.class, () -> ImageAsset.generateNewImageAsset(null));
    }

    @Test
    public void verifyHashCode() {
        final ImageAsset imageAsset = ImageAsset.generateNewImageAsset(TEST_IMAGE_PATH);
        final int expectedHash = Objects.hash(imageAsset.id, TEST_IMAGE_PATH);
        final int actualHash = imageAsset.hashCode();
        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void verifyEquals() {
        final UUID id = UUID.randomUUID();
        final JSONObject assetJSON = new JSONObject();
        assetJSON.put("id", id.toString());
        assetJSON.put("path", TEST_IMAGE_PATH);

        final ImageAsset image1 = ImageAsset.createFromJSON(assetJSON.toString());
        final ImageAsset image2 = ImageAsset.createFromJSON(assetJSON.toString());

        final ImageAsset differentId = ImageAsset.generateNewImageAsset(TEST_IMAGE_PATH);

        final JSONObject differentPathJSON = new JSONObject(assetJSON.toString());
        differentPathJSON.put("path", "different path");
        final ImageAsset differentPath = ImageAsset.createFromJSON(differentPathJSON.toString());

        final ImageAsset completelyDifferent = ImageAsset.generateNewImageAsset("completely different");

        assertEquals(image1, image1);
        assertEquals(image2, image2);

        assertEquals(image1, image2);
        assertEquals(image2, image1);

        this.assertNotEqualsReflective(image1, differentId);
        this.assertNotEqualsReflective(image1, differentPath);
        this.assertNotEqualsReflective(image1, completelyDifferent);

        assertFalse(image1.equals(null));
        assertFalse(image1.equals("some string"));
    }

    private void assertNotEqualsReflective(ImageAsset image1, ImageAsset image2) {
        assertNotEquals(image1, image2);
        assertNotEquals(image2, image1);
    }

}
