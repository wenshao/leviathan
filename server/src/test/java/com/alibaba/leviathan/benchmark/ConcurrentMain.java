package com.alibaba.leviathan.benchmark;

import com.alibaba.leviathan.client.LeviathanClient;

public class ConcurrentMain {

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        {
            String prop = System.getProperty("server");
            if (prop != null && prop.length() > 0) {
                host = prop;
            }
        }
        
        LeviathanClient[] clients = new LeviathanClient[1000 * 40];
        for (int i = 0; i < clients.length; ++i) {
            LeviathanClient client = new LeviathanClient(host, 7002);
            client.connect();
            client.write("hello world");
            client.readString();
        }
        System.out.println("completed.");
        Thread.sleep(1000 * 1000);
    }
}
