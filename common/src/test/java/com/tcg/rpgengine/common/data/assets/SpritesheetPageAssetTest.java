package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.TestUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.*;

public class SpritesheetPageAssetTest {

    private static final UUID TEST_ID = UUID.fromString("866b1e98-65e8-480b-9fd1-f8068ce08cc5");
    private static final String TEST_PATH = "assets/test_page.png";
    private static final int TEST_ROWS = 2;
    private static final int TEST_COLUMNS = 4;
    private static final String TEST_JSON_STRING = "{\n" +
            "  \"id\": \"866b1e98-65e8-480b-9fd1-f8068ce08cc5\",\n" +
            "  \"path\": \"assets/test_page.png\",\n" +
            "  \"rows\": 2,\n" +
            "  \"columns\": 4\n" +
            "}";
    private static final byte[] TEST_BYTES = {
            // id (866b1e98-65e8-480b-9fd1-f8068ce08cc5)
            (byte) 0x86, (byte) 0x6b, (byte) 0x1e, (byte) 0x98,
            (byte) 0x65, (byte) 0xe8,
            (byte) 0x48, (byte) 0x0b,
            (byte) 0x9f, (byte) 0xd1,
            (byte) 0xf8, (byte) 0x06, (byte) 0x8c, (byte) 0xe0, (byte) 0x8c, (byte) 0xc5,
            // Path Length (20)
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14,
            // Test Path (assets/test_page.png)
            (byte) 0x61, (byte) 0x73, (byte) 0x73, (byte) 0x65,
            (byte) 0x74, (byte) 0x73, (byte) 0x2f, (byte) 0x74,
            (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0x5f,
            (byte) 0x70, (byte) 0x61, (byte) 0x67, (byte) 0x65,
            (byte) 0x2e, (byte) 0x70, (byte) 0x6e, (byte) 0x67,
            // Test Rows (2)
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            // Test Columns (4)
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
    };

    @Test
    public void canCreateNewSpritesheetPage() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createNewSpritesheetPageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertNotNull(spritesheetPageAsset);
        assertEquals(TEST_PATH, spritesheetPageAsset.getPath());
        assertEquals(TEST_ROWS, spritesheetPageAsset.rows);
        assertEquals(TEST_COLUMNS, spritesheetPageAsset.columns);
    }

    @Test
    public void cannotCreateSpritesheetWithNullPath() {
        assertThrows(NullPointerException.class, () -> SpritesheetPageAsset.createNewSpritesheetPageAsset(
                null, TEST_ROWS, TEST_COLUMNS
        ));
    }

    @Test
    public void canSetPathOfSpritesheet() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createNewSpritesheetPageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertEquals(TEST_PATH, spritesheetPageAsset.getPath());
        final String expectedPath = "assets/new_path.png";
        spritesheetPageAsset.setPath(expectedPath);
        assertEquals(expectedPath, spritesheetPageAsset.getPath());
    }

    @Test
    public void cannotSetPathOfSpritesheetToNull() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createNewSpritesheetPageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertThrows(NullPointerException.class, () -> spritesheetPageAsset.setPath(null));
    }

    @Test
    public void verifyJSON() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createNewSpritesheetPageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        final JSONObject expectedJSON = new JSONObject(TEST_JSON_STRING);
        expectedJSON.put("id", spritesheetPageAsset.id.toString());
        final JSONObject actualJSON = spritesheetPageAsset.toJSON();
        TestUtils.assertJSONObjectsEquals(expectedJSON, actualJSON);
    }

    @Test
    public void canCreateFromJSON() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createFromJSON(TEST_JSON_STRING);
        assertNotNull(spritesheetPageAsset);
        assertEquals(TEST_ID, spritesheetPageAsset.id);
        assertEquals(TEST_PATH, spritesheetPageAsset.getPath());
        assertEquals(TEST_ROWS, spritesheetPageAsset.rows);
        assertEquals(TEST_COLUMNS, spritesheetPageAsset.columns);
    }

    @Test
    public void verifyNumberOfBytes() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createFromJSON(TEST_JSON_STRING);
        final int expectedLength = TEST_BYTES.length;
        final int actualLength = spritesheetPageAsset.numberOfBytes();
        assertEquals(expectedLength, actualLength);
    }

    @Test
    public void verifyToBytes() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createFromJSON(TEST_JSON_STRING);
        final byte[] actualBytes = spritesheetPageAsset.toBytes();
        assertArrayEquals(TEST_BYTES, actualBytes);
    }

    @Test
    public void canCreateFromBytes() {
        final SpritesheetPageAsset spritesheetPageAsset = SpritesheetPageAsset.createFromBytes(
                ByteBuffer.wrap(TEST_BYTES)
        );
        assertNotNull(spritesheetPageAsset);
        assertEquals(TEST_ID, spritesheetPageAsset.id);
        assertEquals(TEST_PATH, spritesheetPageAsset.getPath());
        assertEquals(TEST_ROWS, spritesheetPageAsset.rows);
        assertEquals(TEST_COLUMNS, spritesheetPageAsset.columns);
    }

}
