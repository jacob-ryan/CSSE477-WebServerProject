package plugins;

import protocol.*;

public interface IServlet
{
	public void start();

	public void stop();

	public void doGet(String rootDir, HttpRequest request, HttpResponse response) throws Exception;
	public boolean doesGet();
	
	public void doPut(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception;
	public boolean doesPut();
	
	public void doDelete(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception;
	public boolean doesDelete();
	
	public void doPost(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception;
	public boolean doesPost();
	
	
	
}