package edu.rosehulman.sws.loadbalancer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable
{

	private Socket clientSocket;
	private Socket webServerSocket;

	public RequestHandler(Socket clientSocket, Socket webServerSocket)
	{
		this.clientSocket = clientSocket;
		this.webServerSocket = webServerSocket;
	}

	@Override
	public void run() 
	{
		try
		{
			InputStream inStream = this.clientSocket.getInputStream();
			OutputStream outStream = this.webServerSocket.getOutputStream();
			
			while (true)
			{
				int ch = inStream.read();
				if (ch >= 0)
				{
					outStream.write(ch);
					outStream.flush();
				}
				else
				{
					throw new IOException();
				}
			}
		}
		catch(IOException e)
		{
			System.out.println(e.toString());	
		}
		
	}

}
