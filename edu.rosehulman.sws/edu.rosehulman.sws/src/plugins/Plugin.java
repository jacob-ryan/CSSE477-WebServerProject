package plugins;

import java.util.*;
import java.util.Map.Entry;

import protocol.*;

/**
 * Used to map to servlets inside the plugin jar
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class Plugin
{
	private String rootUrl;
	private Map<ServletMapping, IServlet> servletMappings;

	public Plugin()
	{
		this.rootUrl = null;
		this.servletMappings = new HashMap<ServletMapping, IServlet>();
	}

	public void setRootUrl(String rootUrl)
	{
		this.rootUrl = rootUrl;
	}

	public String getRootUrl()
	{
		return this.rootUrl;
	}

	public void addServlet(ServletMapping mapping, IServlet servlet)
	{
		if (this.servletMappings.containsKey(mapping))
		{
			throw new IllegalArgumentException("Trying to add duplicate servlet to a plugin: " + mapping);
		}
		else
		{
			this.servletMappings.put(mapping, servlet);
		}
	}

	public void start()
	{
		for (IServlet servlet : this.servletMappings.values())
		{
			servlet.start();
		}
	}

	public void stop()
	{
		for (IServlet servlet : this.servletMappings.values())
		{
			servlet.stop();
		}
	}

	public void processRequest(String serverRootDir, ServletMapping mapping, HttpRequest request, HttpResponse response) throws Exception
	{
		IServlet servlet = this.servletMappings.get(mapping);

		if (servlet != null)
		{
			servlet.processRequest(request, response, serverRootDir);
		}
		else
		{
			for (Entry<ServletMapping, IServlet> entry : this.servletMappings.entrySet())
			{
				System.out.println("Mapping: " + entry.getKey() + "\t" + entry.getValue());
			}
			throw new Exception("No servlets exist to handle sub-URL: " + mapping.getSubUri());
		}
	}
}