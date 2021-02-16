package com.tcg.rpgengine.common.data.system;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class GlobalVariableTest {

    private static final String TEST_NAME = "var_name";
    private static final float TEST_INITIAL_VALUE = 3.14159f;
    private static final byte[] TEST_BYTES = {
            // uuid (1bb6fcca-7c18-43c9-8053-31e357ced6ba)
            (byte) 0x1b, (byte) 0xb6, (byte) 0xfc, (byte) 0xca,
            (byte) 0x7c, (byte) 0x18,
            (byte) 0x43, (byte) 0xc9,
            (byte) 0x80, (byte) 0x53,
            (byte) 0x31, (byte) 0xe3, (byte) 0x57, (byte) 0xce, (byte) 0xd6, (byte) 0xba,
            // initial value (3.14159f)
            (byte) 0x40, (byte) 0x49, (byte) 0x0f, (byte) 0xd0
    };

    @Test
    public void canCreateGlobalVariableFromData() {
        GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(TEST_NAME, TEST_INITIAL_VALUE);
        assertNotNull(globalVariable);
        assertEquals(TEST_NAME, globalVariable.getName());
        assertEquals(TEST_INITIAL_VALUE, globalVariable.initialValue, 1e-9);
    }

    @Test
    public void cannotCreateGlobalVariableWithNullName() {
        assertThrows(NullPointerException.class, () -> GlobalVariable.createNewGlobalVariable(null, 0));
    }

    @Test
    public void canSetName() {
        final String expectedName = "new_name";
        final GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(TEST_NAME, TEST_INITIAL_VALUE);
        globalVariable.setName(expectedName);
        assertEquals(expectedName, globalVariable.getName());
    }

    @Test
    public void cannotSetNameToNull() {
        final GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(TEST_NAME, TEST_INITIAL_VALUE);
        assertThrows(NullPointerException.class, () -> globalVariable.setName(null));
    }

    @Test
    public void verifyToJSON() {
        final GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(TEST_NAME, TEST_INITIAL_VALUE);
        final UUID id = globalVariable.id;
        JSONObject actualJSON = globalVariable.toJSON();
        assertNotNull(actualJSON);
        this.verifyJSONHasFields(actualJSON);
        this.verifyJSONValues(id, actualJSON);
    }

    private void verifyJSONHasFields(JSONObject actualJSON) {
        assertTrue(actualJSON.has("id"));
        assertTrue(actualJSON.has("name"));
        assertTrue(actualJSON.has("initial_value"));
    }

    private void verifyJSONValues(UUID id, JSONObject actualJSON) {
        assertEquals(id.toString(), actualJSON.getString("id"));
        assertEquals(TEST_NAME, actualJSON.getString("name"));
        assertEquals(TEST_INITIAL_VALUE, actualJSON.getFloat("initial_value"), 1e-9);
    }

    @Test
    public void canCreateFromJSON() {
        final UUID id = UUID.randomUUID();
        final GlobalVariable globalVariable = this.createTestGlobalVariableWithId(id);
        assertNotNull(globalVariable);
        assertEquals(id, globalVariable.id);
        assertEquals(TEST_NAME, globalVariable.getName());
        assertEquals(TEST_INITIAL_VALUE, globalVariable.initialValue, 1e-9);
    }

    @Test
    public void verifyContentLength() {
        final GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(TEST_NAME, TEST_INITIAL_VALUE);
        final int expectedContentLength = 20;
        final int actualContentLength = globalVariable.numberOfBytes();
        assertEquals(expectedContentLength, actualContentLength);
    }

    @Test
    public void verifyToBytes() {
        final UUID id = UUID.fromString("1bb6fcca-7c18-43c9-8053-31e357ced6ba");
        final GlobalVariable globalVariable = this.createTestGlobalVariableWithId(id);
        final byte[] actualBytes = globalVariable.toBytes();
        assertArrayEquals(TEST_BYTES, actualBytes);
    }

    private GlobalVariable createTestGlobalVariableWithId(UUID id) {
        final JSONObject srcJson = new JSONObject();
        srcJson.put("id", id.toString());
        srcJson.put("name", TEST_NAME);
        srcJson.put("initial_value", TEST_INITIAL_VALUE);
        return GlobalVariable.createFromJSON(srcJson.toString());
    }

    @Test
    public void createFromBytes() {
        final UUID id = UUID.fromString("1bb6fcca-7c18-43c9-8053-31e357ced6ba");
        final GlobalVariable globalVariable = GlobalVariable.createFromBytes(ByteBuffer.wrap(TEST_BYTES));
        assertNotNull(globalVariable);
        assertEquals(id, globalVariable.id);
        // Global variables loaded from binary will always have a blank name
        assertEquals("", globalVariable.getName());
        assertEquals(TEST_INITIAL_VALUE, globalVariable.initialValue, 1e-9);
    }
}
