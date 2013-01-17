package com.alibaba.leviathan.benchmark;

import java.io.IOException;

import com.alibaba.leviathan.client.LeviathanClient;

public class LocalBenchmarkMain {
    public static void main(String[] args) throws Exception {
        LeviathanClient client = new LeviathanClient("127.0.0.1", 7001);
        client.connect();
        client.write("hello world");

        String resp = client.readString();
        System.out.println(resp);
        
		perf(client);
        client.close();
    }

	private static void perf(LeviathanClient client) throws IOException {
		long startMillis = System.currentTimeMillis();
		
		for (int i = 0; i < 1000; ++i) {
        	client.write("hello world");
        	client.readString();
        }
		
		long millis = System.currentTimeMillis() - startMillis;
		System.out.println("client-millis : " + millis);
	}
}
