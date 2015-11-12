package edu.rosehulman.sws.heartbeat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class HeartbeatMonitor {

	private HashMap<String, BeatSource> currentHeartbeats = new HashMap<>();

	public HeartbeatMonitor(ArrayList<String> connections) {
		for (String connection : connections) {
			startHeartbeat(connection);
		}

//		JFrame frame = new JFrame("Test");
//		JPanel panel = new JPanel();
//		JButton button = new JButton("hello agin1");
//		button.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				for(BeatSource source : HeartbeatMonitor.this.currentHeartbeats.values()) {
//					source.stop();
//				}
//			}
//		});
//		panel.add(button);
//		frame.add(panel);
//		frame.setSize(500, 200);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
		
	}

	public void addConnection(String connection) {
		startHeartbeat(connection);
	}

	public void removeConnection(String connection) {
		if (!this.currentHeartbeats.containsKey(connection)) {
			return;
		}
		this.currentHeartbeats.remove(connection).stop();
	}

	private void startHeartbeat(String connection) {
		String[] connectionParts = connection.split(":");
		String localhost = connectionParts[0];
		int targetPort = Integer.parseInt(connectionParts[1]);
		BeatSource source = new BeatSource(localhost, targetPort);
		this.currentHeartbeats.put(connection, source);
		new Thread(source).start();
	}
	
	public void stop() {
		for(BeatSource source : this.currentHeartbeats.values()) {
			source.stop();
		}
	}
}


