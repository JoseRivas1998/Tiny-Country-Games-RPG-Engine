package com.tcg.rpgengine.common.utils;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UuidUtils {

    private static final Set<UUID> generatedUuids = new HashSet<>();
    public static final int UUID_NUMBER_OF_BYTES = Long.BYTES * 2;

    public static UUID generateUuid() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (generatedUuids.contains(uuid));
        generatedUuids.add(uuid);
        return uuid;
    }

    public static byte[] toBytes(UUID uuid) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[UUID_NUMBER_OF_BYTES]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }


    public static UUID fromBytes(byte[] binaryUuid) {
        if (binaryUuid.length != UUID_NUMBER_OF_BYTES) {
            throw new IllegalArgumentException("UUID bytes must be 16 bytes long.");
        }
        final ByteBuffer byteBuffer = ByteBuffer.wrap(binaryUuid);
        final long mostSignificantBits = byteBuffer.getLong();
        final long leastSignificantBits = byteBuffer.getLong();
        final UUID uuid = new UUID(mostSignificantBits, leastSignificantBits);
        generatedUuids.add(uuid);
        return uuid;
    }

    public static UUID fromString(String uuidString) {
        final UUID uuid = UUID.fromString(uuidString);
        generatedUuids.add(uuid);
        return uuid;
    }
}
