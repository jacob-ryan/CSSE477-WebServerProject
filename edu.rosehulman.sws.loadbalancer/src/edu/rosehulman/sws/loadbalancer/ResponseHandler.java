package edu.rosehulman.sws.loadbalancer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ResponseHandler implements Runnable
{

	private Socket clientSocket;
	private Socket webServerSocket;

	public ResponseHandler(Socket clientSocket, Socket webServerSocket)
	{
		this.clientSocket = clientSocket;
		this.webServerSocket = webServerSocket;
	}

	@Override
	public void run() 
	{
		try
		{
			InputStream inStream = this.webServerSocket.getInputStream();
			OutputStream outStream = this.clientSocket.getOutputStream();
			
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
			try 
			{
				this.clientSocket.close();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
