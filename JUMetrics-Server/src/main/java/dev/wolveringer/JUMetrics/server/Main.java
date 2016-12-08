package dev.wolveringer.JUMetrics.server;

import dev.wolveringer.JUMetrics.server.network.NettySocket;

public class Main {
	public static void main(String[] args) {
		System.out.println("Starting server");
		NettySocket socket = new NettySocket("localhost", 1111);
		socket.start();
		while (socket.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
	}
}
