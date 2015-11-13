package edu.rosehulman.sws.heartbeat;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		ArrayList<String> conns = new ArrayList<>();
		conns.add("137.112.232.41:8080");
		new HeartbeatMonitor(conns);
	}
	
}
