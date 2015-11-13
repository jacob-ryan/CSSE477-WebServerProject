package edu.rosehulman.sws.loadbalancer;

import java.net.Socket;

public class ClientHandler
{

	private LoadBalancer loadBalancer;
	private Socket clientSocket;

	public ClientHandler(LoadBalancer loadBalancer, Socket clientSocket)
	{
		this.loadBalancer = loadBalancer;
		this.clientSocket = clientSocket;
		Socket webServerSocket = loadBalancer.createWebServerSocket();
		System.out.println("Created web server socket");
		
		RequestHandler reqHandler = new RequestHandler(clientSocket, webServerSocket);
		ResponseHandler resHandler = new ResponseHandler(clientSocket, webServerSocket);
		
		new Thread(reqHandler).start();
		new Thread(resHandler).start();
	}
}
