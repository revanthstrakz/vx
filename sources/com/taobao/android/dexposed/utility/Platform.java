package com.taobao.android.dexposed.utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Platform {
    static Platform PLATFORM_INTERNAL;

    static class Platform32Bit extends Platform {
        public int getIntSize() {
            return 4;
        }

        Platform32Bit() {
        }

        public int orderByteToInt(byte[] bArr) {
            return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        public long orderByteToLong(byte[] bArr) {
            return ((long) ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getInt()) & 4294967295L;
        }

        public byte[] orderLongToByte(long j, int i) {
            return ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN).putInt((int) j).array();
        }

        public byte[] orderIntToByte(int i) {
            return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
        }
    }

    static class Platform64Bit extends Platform {
        public int getIntSize() {
            return 8;
        }

        Platform64Bit() {
        }

        public int orderByteToInt(byte[] bArr) {
            return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        public long orderByteToLong(byte[] bArr) {
            return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getLong();
        }

        public byte[] orderLongToByte(long j, int i) {
            return ByteBuffer.allocate(i).order(ByteOrder.LITTLE_ENDIAN).putLong(j).array();
        }

        public byte[] orderIntToByte(int i) {
            return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
        }
    }

    public abstract int getIntSize();

    public abstract int orderByteToInt(byte[] bArr);

    public abstract long orderByteToLong(byte[] bArr);

    public abstract byte[] orderIntToByte(int i);

    public abstract byte[] orderLongToByte(long j, int i);

    static {
        if (Runtime.is64Bit()) {
            PLATFORM_INTERNAL = new Platform64Bit();
        } else {
            PLATFORM_INTERNAL = new Platform32Bit();
        }
    }

    public static Platform getPlatform() {
        return PLATFORM_INTERNAL;
    }
}
