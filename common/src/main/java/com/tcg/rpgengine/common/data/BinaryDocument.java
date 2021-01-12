package com.tcg.rpgengine.common.data;

import com.tcg.rpgengine.common.utils.UuidUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface BinaryDocument {

    byte[] toBytes();
    int numberOfBytes();

    static void putUTF8String(ByteBuffer byteBuffer, String string) {
        final byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        byteBuffer.putInt(stringBytes.length);
        byteBuffer.put(stringBytes);
    }

    static String getUTF8String(ByteBuffer bytes) {
        final int stringLength = bytes.getInt();
        final byte[] stringBytes = new byte[stringLength];
        bytes.get(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

    static UUID getUuid(ByteBuffer bytes) {
        final byte[] idBytes = new byte[UuidUtils.UUID_NUMBER_OF_BYTES];
        bytes.get(idBytes);
        return UuidUtils.fromBytes(idBytes);
    }

}
