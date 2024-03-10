package com.abhi.leximentor.inventory.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class KeyGeneratorUtil {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static long refId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Math.abs(buffer.getLong(0));
    }
}
