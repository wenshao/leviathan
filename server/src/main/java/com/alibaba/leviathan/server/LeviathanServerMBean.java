package com.alibaba.leviathan.server;

public interface LeviathanServerMBean {

    long getClosedCount();

    long getAcceptedCount();

    long getReceivedBytes();
}
