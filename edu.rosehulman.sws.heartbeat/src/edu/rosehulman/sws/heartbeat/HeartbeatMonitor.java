package edu.rosehulman.sws.heartbeat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Date;

public class HeartbeatMonitor {

	private static File logFile = new File("logs/eventlog.txt");

	public static void main(String[] args) {
		try {
			boolean newLogFile = logFile.createNewFile();
			if (newLogFile) {
				FileWriter fw = new FileWriter(logFile, true);
				fw.write("Heartbeat Monitor Logs\n");
				fw.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int targetPort = 8080;
		String localhost = "localhost";
		Socket socket;
		String request = "GET /heartbeat.html HTTP/1.1\n" + "Host: localhost\n" + "Connection: Keep-Alive\n"
				+ "User-Agent: HttpTestClient/1.0\n" + "Accept: text/html,text/plain,application/xml,application/json\n"
				+ "Accept-Language: en-US,en;q=0.8\n\n";
		logEvent("Started Heartbeat Monitor for Simple Web Server (SWS) 1.0.0");
		while (true) {
			try {
				socket = new Socket(localhost, targetPort);
				// Set 10 second timeout for request
				socket.setSoTimeout(10000);
				OutputStream out = socket.getOutputStream();
				InputStream in = socket.getInputStream();
				logEvent("Sending request...");
				out.write(request.getBytes());
				out.flush();
				String checkString = "";
				int ch;
				while ((ch = in.read()) != -1)
					checkString += (char) ch;
				if (!checkString.contains("RUNNING\n")) {
					socket.close();
					throw new Exception("Server not running!");
				} else {
					logEvent("Server running smoothly.");
				}
			} catch (SocketTimeoutException e) {
				logEvent("Connection timed out");
			} catch (Exception e) {
				logEvent(e.getMessage());
			}

			// Sleep for a minute
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
		}
	}

	private static void logEvent(String event) {
		if (event.contains("Started")) {
			System.out.println(event);
		} else {
			System.out.println("[Heartbeat Monitor] " + event);
		}
		Date date = new Date();
		try {
			FileWriter fw = new FileWriter(logFile, true);
			fw.write("[" + new Timestamp(date.getTime()) + "] " + event + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
