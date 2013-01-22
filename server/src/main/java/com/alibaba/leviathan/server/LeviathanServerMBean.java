package com.alibaba.leviathan.server;

public interface LeviathanServerMBean {

    long getClosedCount();

    long getAcceptedCount();
    
    long getSessionCount();

    long getReceivedBytes();
    
    long getReceivedMessageCount();
    
    long getSentBytes();
    
    long getSentMessageCount();
    
    void resetStat();
}
