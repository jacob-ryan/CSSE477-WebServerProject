/*
 * ServletMapping.java
 * Nov 1, 2015
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
 
package plugins;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class ServletMapping
{
	private String method;
	private String subUri;
	
	public ServletMapping(String method, String subUri)
	{
		this.method = method;
		this.subUri = subUri;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public String getSubUri()
	{
		return this.subUri;
	}
	
	@Override
	public String toString()
	{
		return "ServletMapping: method=" + this.method + ", subUri=" + this.subUri;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof ServletMapping)
		{
			ServletMapping other = (ServletMapping) object;
			boolean a = this.method.equalsIgnoreCase(other.method);
			boolean b = this.subUri.equalsIgnoreCase(other.subUri);
			return a && b;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int a = this.method.toLowerCase().hashCode();
		int b = this.subUri.toLowerCase().hashCode();
		return a * 31 + b;
	}
}