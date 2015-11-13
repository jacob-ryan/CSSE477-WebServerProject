/*
 * ConnectionRateLimiter.java
 * Nov 2, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package edu.rosehulman.sws.loadbalancer;

import java.net.*;
import java.util.*;

public class ConnectionRateLimiter extends Thread
{
	private static ConnectionRateLimiter instance;
	
	public static ConnectionRateLimiter getInstance()
	{
		if (ConnectionRateLimiter.instance == null)
		{
			ConnectionRateLimiter.instance = new ConnectionRateLimiter();
		}
		return ConnectionRateLimiter.instance;
	}
	
	private Map<InetAddress, Integer> connections;
	
	public ConnectionRateLimiter()
	{
		this.connections = new HashMap<InetAddress, Integer>();
		start();
	}
	
	public synchronized boolean canConnect(InetAddress remoteAddress)
	{
		Integer count = this.connections.get(remoteAddress);
		if (count == null)
		{
			count = 0;
		}
		count += 1;
		this.connections.put(remoteAddress, count);
		
		return count < 100;
	}
	
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Thread.sleep(10 * 1000);
				synchronized (this)
				{
					this.connections = new HashMap<InetAddress, Integer>();
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}