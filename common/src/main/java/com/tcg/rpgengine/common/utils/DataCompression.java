package com.tcg.rpgengine.common.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DataCompression {

    private static final int BUFFER_SIZE = 1024;

    public static byte[] compress(byte[] bytes) {
        final Deflater deflater = new Deflater();
        deflater.setInput(bytes);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
        deflater.finish();
        final byte[] buffer = new byte[BUFFER_SIZE];
        while (!deflater.finished()) {
            final int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] bytes) {
        try {
            final Inflater inflater = new Inflater();
            inflater.setInput(bytes);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
            final byte[] buffer = new byte[BUFFER_SIZE];
            while (!inflater.finished()) {
                final int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
