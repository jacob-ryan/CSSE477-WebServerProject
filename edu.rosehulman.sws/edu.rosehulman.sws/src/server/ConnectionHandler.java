/*
 * ConnectionHandler.java Oct 7, 2012 Simple Web Server (SWS) for CSSE 477 Copyright (C) 2012 Chandan Raj Rupakheti This program is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You
 * should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/lgpl.html>.
 */

package server;

import java.io.*;
import java.net.*;

import plugins.*;
import protocol.*;
import protocol.ProtocolException;

/**
 * This class is responsible for handling a incoming request by creating a {@link HttpRequest} object and sending the appropriate response be creating
 * a {@link HttpResponse} object. It implements {@link Runnable} to be used in multi-threaded environment.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable
{
	private Server server;
	private Socket socket;

	public ConnectionHandler(Server server, Socket socket)
	{
		this.server = server;
		this.socket = socket;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket()
	{
		return this.socket;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request and creates a {@link HttpRequest} object, then it creates an
	 * appropriate {@link HttpResponse} object and sends the response back to the client (web browser).
	 */
	@Override
	public void run()
	{
		// Get the start time
		long start = System.currentTimeMillis();
		
		try
		{
			InputStream inStream = this.socket.getInputStream();
			OutputStream outStream = this.socket.getOutputStream();
			handleRequest(inStream, outStream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.server.incrementConnections(1);
			long end = System.currentTimeMillis();
			this.server.incrementServiceTime(end - start);
			try
			{
				this.socket.close();
			}
			catch (Exception e)
			{
				// Do nothing.
			}
		}
	}

	private void handleRequest(InputStream inStream, OutputStream outStream) throws Exception
	{
		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try
		{
			System.out.println("[ConnectionHandler] Received request, calling HttpRequest.read().");
			request = HttpRequest.read(inStream);
			System.out.println("[ConnectionHandler] Successfully processed request:\n" + request);
		}
		catch (ProtocolException pe)
		{
			pe.printStackTrace();
			// We have some sort of protocol exception. Get its status code and create response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if (status == Protocol.BAD_REQUEST_CODE)
			{
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
			else if (status == Protocol.NOT_SUPPORTED_CODE)
			{
				response = HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}

		if (response != null)
		{
			// Means there was an error, now write the response object to the socket
			response.write(outStream);
			return;
		}

		// We reached here means no error so far, so lets process further
		try
		{
			System.out.println("[ConnectionHandler] Valid request, parsing URI: " + request.getUri());
			// Actually process the request and create a response.
			response = HttpResponseFactory.createSuccess(null, Protocol.CLOSE, 201);

			int endIndex = request.getUri().indexOf("/", 1);
			if (endIndex != -1)
			{
				String rootUrl = request.getUri().substring(1, endIndex);
				System.out.println("Searching for plugin to handle rootUrl=" + rootUrl);
				Plugin plugin = PluginManager.instance.getPlugin(rootUrl);
				if (plugin != null)
				{
					String subUrl = request.getUri().substring(endIndex + 1);
					plugin.processRequest(subUrl, request, response);
				}
				else
				{
					System.out.println("Couldn't find plugin to handle request!!!");
				}
			}
			else
			{
				//Process static file
				response = request.handleRequest(this.server.getRootDirectory());
				System.out.println("Should be serving static file here!!!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// TODO: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if (response == null)
		{
			System.out.println("Response is still null!!!");
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}

		try
		{
			// Write response and we are all done so close the socket
			response.write(outStream);
			System.out.println(response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}