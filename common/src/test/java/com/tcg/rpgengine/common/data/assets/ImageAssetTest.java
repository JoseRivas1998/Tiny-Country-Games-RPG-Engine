package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

import static com.tcg.rpgengine.common.TestUtils.assertJSONObjectsEquals;
import static org.junit.Assert.*;

public class ImageAssetTest {

    private static final String TEST_IMAGE_PATH = "test_image.png";
    private static final byte[] TEST_BYTES = {
            // uuid (8f05d644-b2ac-4da8-9359-624b4ea11fed)
            (byte) 0x8f, (byte) 0x05, (byte) 0xd6, (byte) 0x44,
            (byte) 0xb2, (byte) 0xac,
            (byte) 0x4d, (byte) 0xa8,
            (byte) 0x93, (byte) 0x59,
            (byte) 0x62, (byte) 0x4b, (byte) 0x4e, (byte) 0xa1, (byte) 0x1f, (byte) 0xed,
            // path length (14)
            0x00, 0x00, 0x00, 0x0e,
            // path
            (byte) 0x74, (byte) 0x65, (byte) 0x73, (byte) 0x74,
            (byte) 0x5f, (byte) 0x69, (byte) 0x6d, (byte) 0x61,
            (byte) 0x67, (byte) 0x65, (byte) 0x2e, (byte) 0x70,
            (byte) 0x6e, (byte) 0x67
    };

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

    @Test
    public void verifyContentLength() {
        final ImageAsset imageAsset = ImageAsset.generateNewImageAsset(TEST_IMAGE_PATH);
        final int expected = 18;
        final int actual = imageAsset.contentLength();
        assertEquals(expected, actual);
    }

    @Test
    public void verifyBytes() {
        final UUID imageId = UUID.fromString("8f05d644-b2ac-4da8-9359-624b4ea11fed");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", imageId.toString());
        jsonObject.put("path", TEST_IMAGE_PATH);
        final ImageAsset imageAsset = ImageAsset.createFromJSON(jsonObject.toString());
        final byte[] actualBytes = imageAsset.toBytes();
        assertArrayEquals(TEST_BYTES, actualBytes);
    }

    @Test
    public void verifyDecode() {
        final UUID imageId = UUID.fromString("8f05d644-b2ac-4da8-9359-624b4ea11fed");
        ImageAsset image = ImageAsset.createFromBytes(ByteBuffer.wrap(TEST_BYTES));
        assertEquals(imageId, image.id);
        assertEquals(TEST_IMAGE_PATH, image.path);
    }

}
