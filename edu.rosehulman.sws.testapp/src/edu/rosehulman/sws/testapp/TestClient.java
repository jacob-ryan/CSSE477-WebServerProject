package edu.rosehulman.sws.testapp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class TestClient {

	private JTextField portNumField;
	private JTextField hostField;
	private Socket socket;
	private JPanel panel;
	private String hostname;
	private int portNum;
	private JTextField numRequestCycleField;

	public TestClient() {
		JFrame frame = new JFrame("Test");
		this.panel = new JPanel();
		this.panel.setBorder(BorderFactory.createTitledBorder("Test Client"));
		this.panel.setLayout(new SpringLayout());

		JLabel portLabel = new JLabel("Port Number: ");
		this.portNumField = new JTextField("8080");
		this.portNumField.setPreferredSize(new Dimension(400, 21));
		this.panel.add(portLabel);
		portLabel.setLabelFor(this.portNumField);
		this.panel.add(this.portNumField);

		JLabel hostLabel = new JLabel("Host: ");
		this.hostField = new JTextField("137.112.226.31");
		this.hostField.setSize(new Dimension(400, 21));
		this.panel.add(hostLabel);
		portLabel.setLabelFor(this.hostField);
		this.panel.add(this.hostField);

		JLabel numRequestLabel = new JLabel("Number of Request Cycles: ");
		this.numRequestCycleField = new JTextField("1");
		// this.numRequestCycleField.setSize(new Dimension(400, 21));
		this.panel.add(numRequestLabel);
		portLabel.setLabelFor(this.numRequestCycleField);
		this.panel.add(this.numRequestCycleField);

		this.panel.add(new JLabel(""));
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TestClient.this.makeConnection(TestClient.this.hostField.getText(),
						Integer.parseInt(TestClient.this.portNumField.getText()));
			}
		});
		this.panel.add(connect);
		this.panel.add(new JLabel(""));
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TestClient.this.runClient(Integer.parseInt(TestClient.this.numRequestCycleField.getText()));
			}
		});
		this.panel.add(start);
		// Lay out the panel.
		SpringUtilities.makeCompactGrid(this.panel, 5, 2, // rows, cols
				5, 5, // initX, initY
				5, 5);
		frame.add(this.panel);
		frame.setSize(500, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void makeConnection(String host, int port) {
		System.out.println(host + ":" + port);
		try {
			if (this.socket != null)
				this.socket.close();
		} catch (Exception e) {
			System.out.println(e.toString() + "\n");
		}

		try {
			this.socket = new Socket(host, port);
			this.hostname = host;
			this.portNum = port;
			System.out.println("Connection Established!\n");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.panel, e.getMessage() + "Connection Closed by the Server!",
					"Connection Problem", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String fillAnimations() {
		Random random = new Random();
		int animationLength = 10;
		String toReturn = "";
		for(int i = 0; i < animationLength; i++) {
			int rand = random.nextInt(10);
			toReturn += rand + "";
		}
		return toReturn;
	}
	
	private void runClient(int numRequestCycles) {
		Random random = new Random();
		int rand = random.nextInt(100);
		String fileName = "foobar" + rand;
		String animations = fillAnimations();
		String postBody = "{'name': '" + fileName + "', 'animations': " + animations
				+ ", 'dateCreated': '9/29/15', 'author': 'David Mutchler', 'description': 'A person dancing.'}\n";
		String postRequest = "POST /RestAPI/Animation HTTP/1.1\n" + "Host: " + this.hostname + "\n"
				 + "Content-Length: " + postBody.length() + "\n"
				 + "Connection: Keep-Alive\n" + "User-Agent: MaliciousTestClient/1.0\n"
				+ "Accept: text/html,text/plain,application/xml,application/json\n"
				+ "Accept-Language: en-US,en;q=0.8\n\n" + postBody;
		System.out.println(postRequest);
		try {
			int count = numRequestCycles;
			long startTime = System.nanoTime();
			int numRequestsSent = 0;
			while (count > 0) {
				this.socket = new Socket(this.hostname, this.portNum);
				OutputStream out = this.socket.getOutputStream();
				InputStream in = this.socket.getInputStream();
				out.write(postRequest.getBytes());
				out.flush();
				numRequestsSent++;
//				System.out.println("POST sent, waiting for response...");
				HttpResponse response = HttpResponse.read(in);
				int id = Integer.parseInt(new String(response.getBody()));
				String[] requestPattern = generateRequestPattern(id);
				Thread.sleep(500);
				for (String request : requestPattern) {
					if(request.contains("PUT")) {
						System.out.println(request);
					}
					this.socket = new Socket(this.hostname, this.portNum);
					out = this.socket.getOutputStream();
					out.write(request.getBytes());
					out.flush();
					numRequestsSent++;
					System.out.println("Next request sent");
					Thread.sleep(500);
				}
				long timeElapsed = System.nanoTime() - startTime;
				count--;
				double requestRate = (double) numRequestsSent / ((double) timeElapsed / 1000000000);
				System.out.println("I've sent " + numRequestsSent + " requests. My current request rate is " + requestRate + " requests/second.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String[] generateRequestPattern(int id) {
		String putBody = "{'id': " + id + ", 'name': 'Person dance', 'animations': '', 'dateCreated': '9/29/15', 'author': 'David Mutchler', 'description': 'A person dancing.'}\n";
		String putRequest = "PUT /RestAPI/Animation HTTP/1.1\n" + "Host: " + this.hostname + "\n"
				 + "Content-Length: " + putBody.length() + "\n"
				 + "Connection: Keep-Alive\n" + "User-Agent: MaliciousTestClient/1.0\n"
				+ "Accept: text/html,text/plain,application/xml,application/json\n"
				+ "Accept-Language: en-US,en;q=0.8\n\n" + putBody;
		String getRequest = "GET /RestAPI/Animation/" + id + " HTTP/1.1\n" + "Host: " + this.hostname + "\n"
				+ "Connection: Keep-Alive\n" + "User-Agent: MaliciousTestClient/1.0\n"
				+ "Accept: text/html,text/plain,application/xml,application/json\n"
				+ "Accept-Language: en-US,en;q=0.8\n\n";
		String deleteRequest = "DELETE /RestAPI/Animation/" + id + " HTTP/1.1\n" + "Host: " + this.hostname + "\n"
				+ "Connection: Keep-Alive\n" + "User-Agent: MaliciousTestClient/1.0\n"
				+ "Accept: text/html,text/plain,application/xml,application/json\n"
				+ "Accept-Language: en-US,en;q=0.8\n\n";
		String[] toReturn = new String[] { getRequest, deleteRequest };
		return toReturn;
	}

	public static void main(String[] args) {
		new TestClient();
	}
}
