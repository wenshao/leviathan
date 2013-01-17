package com.alibaba.leviathan.message;


public interface XMessage {
    byte[] toBytes();
    short getTag();
}
