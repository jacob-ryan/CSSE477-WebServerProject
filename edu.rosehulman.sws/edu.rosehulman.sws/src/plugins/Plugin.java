package plugins;

import java.util.*;

import protocol.*;

public class Plugin {
	private String rootUrl;
	private Map<String, IServlet> servletMappings;

	public Plugin() {
		this.rootUrl = null;
		this.servletMappings = new HashMap<String, IServlet>();
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public String getRootUrl() {
		return this.rootUrl;
	}

	public void addServlet(String uri, IServlet servlet) {
		// ignore for now, later will ask user which uri to load.
		if (!this.servletMappings.containsKey(uri)) {
			this.servletMappings.put(uri, servlet);
		}
	}

	public void start() {
		for (IServlet servlet : this.servletMappings.values()) {
			servlet.start();
		}
	}

	public void stop() {
		for (IServlet servlet : this.servletMappings.values()) {
			servlet.stop();
		}
	}

	public void processRequest(String serverRootDir, String subUrl, HttpRequest request, HttpResponse response) throws Exception {
		if(subUrl.contains("/"))
		{
			subUrl = subUrl.substring(0, subUrl.indexOf("/"));
		}
		IServlet servlet = this.servletMappings.get(subUrl);
		
		if (servlet != null) {
			switch(request.getMethod())
			{
			case Protocol.GET:
				if(servlet.doesGet())
				{
					servlet.doGet(serverRootDir, request, response);
				}
				else
				{
					System.out.println("Servlet does not implement request method " + request.getMethod());
				}
				break;
			case Protocol.DELETE:
				if(servlet.doesDelete())
				{
					servlet.doDelete(serverRootDir, request, response);
				}
				else
				{
					System.out.println("Servlet does not implement request method " + request.getMethod());
				}
				break;
			case Protocol.PUT:
				if(servlet.doesPut())
				{
					servlet.doPut(serverRootDir, request, response);
				}
				else
				{
					System.out.println("Servlet does not implement request method " + request.getMethod());
				}
				break;
			case Protocol.POST:
				if(servlet.doesPost())
				{
					servlet.doPost(serverRootDir, request, response);
				}
				else
				{
					System.out.println("Servlet does not implement request method " + request.getMethod());
				}
				break;
			default:
				System.out.println("Unsupported method type");
				break;

			}
			
		} else {
			throw new Exception("No servlets exist to handle sub-URL: " + subUrl);
		}
	}
}