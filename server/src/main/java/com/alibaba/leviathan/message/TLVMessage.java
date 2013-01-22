package com.alibaba.leviathan.message;


public interface TLVMessage {
    byte[] toBytes();
    short getTag();
}
