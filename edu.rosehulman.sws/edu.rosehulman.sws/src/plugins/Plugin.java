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

	public void processRequest(String subUrl, HttpRequest request, HttpResponse response) throws Exception {
		IServlet servlet = this.servletMappings.get(subUrl);
		if (servlet != null) {
			servlet.processRequest(request.getMethod(), request, response);
		} else {
			throw new Exception("No servlets exist to handle sub-URL: " + subUrl);
		}
	}
}