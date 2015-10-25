/*
 * PostRequest.java Oct 18, 2015 Simple Web Server (SWS) for EE407/507 and CS455/555 Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl.html>. Contact Us: Chandan Raj Rupakheti (rupakhcr@clarkson.edu) Department of Electrical and Computer
 * Engineering Clarkson University Potsdam NY 13699-5722 http://clarkson.edu/~rupakhcr
 */

package protocol;

import java.io.*;

/**
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PostRequest extends HttpRequest
{

	/**
	 * @param uri
	 * @param version
	 */
	public PostRequest(String uri, String version)
	{
		super(uri, version);
	}

	/*
	 * (non-Javadoc)
	 * @see protocol.HttpRequest#handleRequest(java.lang.String)
	 */
	@Override
	public HttpResponse handleRequest(String rootDirectory)
	{
		File file = new File(rootDirectory + getUri());

		if (file.exists() && file.isDirectory())
		{
			return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}

		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			try (FileWriter writer = new FileWriter(file, true))
			{
				writer.write(getBody());
			}
		}
		catch (IOException e)
		{
			return HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
		}

		return HttpResponseFactory.createSuccess(file, Protocol.CLOSE, getSuccessCode());
	}

	/*
	 * (non-Javadoc)
	 * @see protocol.HttpRequest#getMethod()
	 */
	@Override
	public String getMethod()
	{
		return Protocol.POST;
	}

	/*
	 * (non-Javadoc)
	 * @see protocol.HttpRequest#getSuccessCode()
	 */
	@Override
	public int getSuccessCode()
	{
		return Protocol.CREATED_CODE;
	}

	/*
	 * (non-Javadoc)
	 * @see protocol.HttpRequest#getSuccessText()
	 */
	@Override
	public String getSuccessText()
	{
		return Protocol.CREATED_TEXT;
	}

}
