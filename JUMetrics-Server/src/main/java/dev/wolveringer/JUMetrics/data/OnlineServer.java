package dev.wolveringer.JUMetrics.data;

import java.util.ArrayList;

import java.util.List;

public class OnlineServer {
	private static final int SERVER_TIMEOUT = 10 * 60 * 60 * 1000;
	private static final List<OnlineServer> server = new ArrayList<OnlineServer>();
	
	private String sessionId;
	private long lastUpate = System.currentTimeMillis();
	
	public void updated(){
		lastUpate = System.currentTimeMillis();
	}
}
