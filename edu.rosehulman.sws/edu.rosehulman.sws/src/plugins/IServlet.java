package plugins;

import protocol.*;

public interface IServlet
{
	public void start();

	public void stop();

	public void processRequest(String method, HttpRequest request, HttpResponse response) throws Exception;
}