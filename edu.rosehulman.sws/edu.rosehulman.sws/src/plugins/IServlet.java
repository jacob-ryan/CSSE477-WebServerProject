package plugins;

import protocol.*;

/**
 * Used to process requests, implemented by user
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public interface IServlet
{
	public void start();

	public void stop();

	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory);
}