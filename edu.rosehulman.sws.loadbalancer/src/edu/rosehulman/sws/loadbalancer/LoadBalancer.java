package edu.rosehulman.sws.loadbalancer;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;




public class LoadBalancer implements Runnable
{
	Host[] webServerAddresses = new Host[]{new Host("137.112.232.41", 8080), new Host("localhost", 8082)};

	int currentWebServerIndex = 0;
	
	private int port;
	private ServerSocket welcomeSocket;
	private long connections;
	private long serviceTime;
	private boolean stop;
	
	public LoadBalancer(int port)
	{
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
	}
	
	public static void main(String[] args)
	{
		LoadBalancer loadBalancer = new LoadBalancer(8080);
		new Thread(loadBalancer).start();
	}

	@Override
	public void run() 
	{
		try
		{
			this.welcomeSocket = new ServerSocket(this.port);

			// Now keep welcoming new connections until stop flag is set to true
			while (true)
			{
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();

				// Come out of the loop if the stop flag is set
				if (this.stop)
				{
					break;
				}
				
				if (ConnectionRateLimiter.getInstance().canConnect(connectionSocket.getInetAddress()))
				{
					System.out.println("creating handler");
					// Create a handler for this incoming connection and start the handler in a new thread
					ClientHandler handler = new ClientHandler(this, connectionSocket);
				}
				else
				{
					// Possible DDoS attack, drop connection immediately.
					connectionSocket.close();
					System.out.println("Detected possible DDoS attack, dropped connection from: " + connectionSocket.getInetAddress());
				}
			}
			this.welcomeSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	
	}
	
	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop()
	{
		if (this.stop)
		{
			return;
		}

		// Set the stop flag to be true
		this.stop = true;
		try
		{
			// This will force welcomeSocket to come out of the blocked accept() method
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), this.port);

			// We do not have any other job for this socket so just close it
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the server is stopeed or not.
	 *
	 * @return
	 */
	public boolean isStoped()
	{
		if (this.welcomeSocket != null)
		{
			return this.welcomeSocket.isClosed();
		}
		return true;
	}

	public Socket createWebServerSocket()
	{
		int index;
		synchronized(this)
		{
			this.currentWebServerIndex = (this.currentWebServerIndex + 1) % this.webServerAddresses.length;
			index = this.currentWebServerIndex;
		}
		Host host = this.webServerAddresses[index];
		try {
			Socket webServerSocket = new Socket(host.ip, host.port);
			return webServerSocket;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	

}
