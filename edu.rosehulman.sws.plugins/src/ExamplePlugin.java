import java.io.*;

import plugins.*;
import protocol.*;

public class ExamplePlugin implements IServlet
{
	@Override
	public void start()
	{
		System.out.println("ExamplePlugin is starting...");
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stop()
	{
		System.out.println("ExamplePlugin is stopping...");
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void processRequest(String method, HttpRequest request, HttpResponse response) throws Exception
	{
		System.out.println("ExamplePlugin received request: " + method + " @ " + request.getUri());
		response.setFile(new File("./plugins/registry.txt"));
	}
}