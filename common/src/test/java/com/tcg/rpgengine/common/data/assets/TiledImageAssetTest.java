package com.tcg.rpgengine.common.data.assets;

import com.tcg.rpgengine.common.TestUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.*;

public class TiledImageAssetTest {

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
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createNewTiledImageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertNotNull(tiledImageAsset);
        assertEquals(TEST_PATH, tiledImageAsset.getPath());
        assertEquals(TEST_ROWS, tiledImageAsset.rows);
        assertEquals(TEST_COLUMNS, tiledImageAsset.columns);
    }

    @Test
    public void cannotCreateSpritesheetWithNullPath() {
        assertThrows(NullPointerException.class, () -> TiledImageAsset.createNewTiledImageAsset(
                null, TEST_ROWS, TEST_COLUMNS
        ));
    }

    @Test
    public void canSetPathOfSpritesheet() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createNewTiledImageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertEquals(TEST_PATH, tiledImageAsset.getPath());
        final String expectedPath = "assets/new_path.png";
        tiledImageAsset.setPath(expectedPath);
        assertEquals(expectedPath, tiledImageAsset.getPath());
    }

    @Test
    public void cannotSetPathOfSpritesheetToNull() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createNewTiledImageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        assertThrows(NullPointerException.class, () -> tiledImageAsset.setPath(null));
    }

    @Test
    public void verifyJSON() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createNewTiledImageAsset(
                TEST_PATH, TEST_ROWS, TEST_COLUMNS
        );
        final JSONObject expectedJSON = new JSONObject(TEST_JSON_STRING);
        expectedJSON.put("id", tiledImageAsset.id.toString());
        final JSONObject actualJSON = tiledImageAsset.toJSON();
        TestUtils.assertJSONObjectsEquals(expectedJSON, actualJSON);
    }

    @Test
    public void canCreateFromJSON() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createFromJSON(TEST_JSON_STRING);
        assertNotNull(tiledImageAsset);
        assertEquals(TEST_ID, tiledImageAsset.id);
        assertEquals(TEST_PATH, tiledImageAsset.getPath());
        assertEquals(TEST_ROWS, tiledImageAsset.rows);
        assertEquals(TEST_COLUMNS, tiledImageAsset.columns);
    }

    @Test
    public void verifyNumberOfBytes() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createFromJSON(TEST_JSON_STRING);
        final int expectedLength = TEST_BYTES.length;
        final int actualLength = tiledImageAsset.numberOfBytes();
        assertEquals(expectedLength, actualLength);
    }

    @Test
    public void verifyToBytes() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createFromJSON(TEST_JSON_STRING);
        final byte[] actualBytes = tiledImageAsset.toBytes();
        assertArrayEquals(TEST_BYTES, actualBytes);
    }

    @Test
    public void canCreateFromBytes() {
        final TiledImageAsset tiledImageAsset = TiledImageAsset.createFromBytes(
                ByteBuffer.wrap(TEST_BYTES)
        );
        assertNotNull(tiledImageAsset);
        assertEquals(TEST_ID, tiledImageAsset.id);
        assertEquals(TEST_PATH, tiledImageAsset.getPath());
        assertEquals(TEST_ROWS, tiledImageAsset.rows);
        assertEquals(TEST_COLUMNS, tiledImageAsset.columns);
    }

}
