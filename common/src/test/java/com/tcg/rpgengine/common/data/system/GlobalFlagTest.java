package com.tcg.rpgengine.common.data.system;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.*;

public class GlobalFlagTest {

    private static final UUID TEST_ID = UUID.fromString("fc45a92c-e8fb-437a-81ce-cfff98c5e346");
    private static final String TEST_NAME = "flag_name";
    private static final boolean TEST_INITIAL_VALUE = true;
    private static final String TEST_JSON = "{" +
            "\"id\": \"fc45a92c-e8fb-437a-81ce-cfff98c5e346\"," +
            "\"name\": \"flag_name\"," +
            "\"initial_value\": true" +
            "}";
    private static final byte[] TEST_BYTES = {
            // id (fc45a92c-e8fb-437a-81ce-cfff98c5e346)
            (byte) 0xfc, (byte) 0x45, (byte) 0xa9, (byte) 0x2c,
            (byte) 0xe8, (byte) 0xfb,
            (byte) 0x43, (byte) 0x7a,
            (byte) 0x81, (byte) 0xce,
            (byte) 0xcf, (byte) 0xff, (byte) 0x98, (byte) 0xc5, (byte) 0xe3, (byte) 0x46,
            // initial value (true)
            (byte) 0xff,
    };

    @Test
    public void createGlobalFlagFromData() {
        final GlobalFlag globalFlag = GlobalFlag.createNewGlobalFlag(TEST_NAME, TEST_INITIAL_VALUE);
        assertNotNull(globalFlag);
        assertNotNull(globalFlag.id);
        assertEquals(TEST_NAME, globalFlag.getName());
        assertEquals(TEST_INITIAL_VALUE, globalFlag.initialValue);
    }

    @Test
    public void cannotCreateGlobalFlagWithNullName() {
        assertThrows(NullPointerException.class, () -> GlobalFlag.createNewGlobalFlag(null, TEST_INITIAL_VALUE));
    }

    @Test
    public void canSetName() {
        final String expectedName = "new_name";
        final GlobalFlag globalFlag = GlobalFlag.createNewGlobalFlag(TEST_NAME, TEST_INITIAL_VALUE);
        globalFlag.setName(expectedName);
        assertEquals(expectedName, globalFlag.getName());
    }

    @Test
    public void cannotSetNameToNull() {
        final GlobalFlag globalFlag = GlobalFlag.createNewGlobalFlag(TEST_NAME, TEST_INITIAL_VALUE);
        assertNotNull(globalFlag);
        assertThrows(NullPointerException.class, () -> globalFlag.setName(null));
    }

    @Test
    public void verifyToJSON() {
        final GlobalFlag globalFlag = GlobalFlag.createNewGlobalFlag(TEST_NAME, TEST_INITIAL_VALUE);
        final UUID id = globalFlag.id;
        final JSONObject actualJSON = globalFlag.toJSON();
        assertNotNull(actualJSON);

        assertTrue(actualJSON.has("id"));
        assertTrue(actualJSON.has("name"));
        assertTrue(actualJSON.has("initial_value"));

        assertEquals(id.toString(), actualJSON.getString("id"));
        assertEquals(TEST_NAME, actualJSON.getString("name"));
        assertEquals(TEST_INITIAL_VALUE, actualJSON.getBoolean("initial_value"));
    }

    @Test
    public void canCreateFromJSON() {
        final GlobalFlag globalFlag = GlobalFlag.createFromJSON(TEST_JSON);
        assertNotNull(globalFlag);
        assertEquals(TEST_ID, globalFlag.id);
        assertEquals(TEST_NAME, globalFlag.getName());
        assertEquals(TEST_INITIAL_VALUE, globalFlag.initialValue);
    }

    @Test
    public void verifyContentLength() {
        final GlobalFlag globalFlag = GlobalFlag.createFromJSON(TEST_JSON);
        final int expectedContentLength = 17;
        final int actualContentLength = globalFlag.numberOfBytes();
        assertEquals(expectedContentLength, actualContentLength);
    }

    @Test
    public void verifyToBytes() {
        final GlobalFlag globalFlag = GlobalFlag.createFromJSON(TEST_JSON);
        final byte[] bytes = globalFlag.toBytes();
        assertArrayEquals(TEST_BYTES, bytes);
    }

    @Test
    public void canCreateFromBytes() {
        final GlobalFlag globalFlag = GlobalFlag.createFromBytes(ByteBuffer.wrap(TEST_BYTES));
        assertNotNull(globalFlag);
        assertEquals(TEST_ID, globalFlag.id);
        assertEquals("", globalFlag.getName());
        assertEquals(TEST_INITIAL_VALUE, globalFlag.initialValue);
    }

}
