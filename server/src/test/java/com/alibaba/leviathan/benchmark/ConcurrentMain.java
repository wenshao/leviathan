package com.alibaba.leviathan.benchmark;

import com.alibaba.leviathan.client.LeviathanClient;

public class ConcurrentMain {

    public static void main(String[] args) throws Exception {
        LeviathanClient[] clients = new LeviathanClient[1000 * 100];
        for (int i = 0; i < clients.length; ++i) {
            LeviathanClient client = new LeviathanClient("127.0.0.1", 7002);
            client.connect();
            client.write("hello world");
            client.readString();
        }
        System.out.println("completed.");
        Thread.sleep(1000 * 1000);
    }
}
