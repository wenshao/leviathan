package com.alibaba.leviathan.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.leviathan.server.LeviathanServer;

public class LeviathanServletContextListener implements ServletContextListener {
	private LeviathanServer server;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		server = new LeviathanServer();
		server.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		server.stop();
	}
}
