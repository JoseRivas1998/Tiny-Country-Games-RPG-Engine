package com.tcg.rpgengine.common.utils;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class UuidUtilsTest {

    static final String TEST_UUID_STRING = "1ae004cf-0f48-469f-8a94-01339afaec41";
    static final byte[] TEST_UUID_BINARY = {
            0x1a, (byte)0xe0, 0x04, (byte)0xcf,
            0x0f, 0x48,
            0x46, (byte)0x9f,
            (byte)0x8a, (byte)0x94,
            0x01, 0x33, (byte)0x9a, (byte)0xfa, (byte)0xec, (byte)0x41
    };

    @Test
    public void canGenerateRandomUuid() {
        UUID uuid = UuidUtils.generateUuid();
        assertNotNull(uuid);
    }

    @Test
    public void writingToBytesGeneratesCorrectByteArray() {
        final byte[] actual = UuidUtils.toBytes(UUID.fromString(TEST_UUID_STRING));
        assertEquals(16, actual.length);
        assertArrayEquals(TEST_UUID_BINARY, actual);
    }

    @Test
    public void readingFromBytesGeneratesCorrectUUID() {
        final UUID expected = UUID.fromString(TEST_UUID_STRING);
        final UUID actual = UuidUtils.fromBytes(TEST_UUID_BINARY);
        assertEquals(expected, actual);
    }

    @Test
    public void uuidUtilsContainsByteLength() {
        assertEquals(16, UuidUtils.UUID_NUMBER_OF_BYTES);
    }

    @Test
    public void cannotCreateAnUUIDFromInvalidByteSizes() {
        assertThrows(IllegalArgumentException.class, () -> UuidUtils.fromBytes(new byte[15]));
        assertThrows(IllegalArgumentException.class, () -> UuidUtils.fromBytes(new byte[17]));
    }

    @Test
    public void canCreateUUIDFromString() {
        final UUID expected = UUID.fromString(TEST_UUID_STRING);
        final UUID actual = UuidUtils.fromString(TEST_UUID_STRING);
        assertEquals(expected, actual);
    }

    

}
