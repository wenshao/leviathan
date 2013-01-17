package com.alibaba.leviathan.message.codec;

public class Bits {

    public static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16)
               + ((b[off + 0]) << 24);
    }

    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val >>> 0);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 0] = (byte) (val >>> 24);
    }

    public static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
    }

    public static short getShort(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0]) << 8));
    }

}
