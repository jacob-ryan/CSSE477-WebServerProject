import java.io.*;

import plugins.*;
import protocol.*;

public class DefaultServlet implements IServlet
{
	@Override
	public void start()
	{
		System.out.println("DefaultPlugin is starting...");
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
		System.out.println("DefaultPlugin is stopping...");
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
	public void doGet(String serverRootDir, HttpRequest request, HttpResponse response)
			throws Exception 
			{
		String filePath = serverRootDir + parseFileName(request.getUri());
		File file = new File(filePath);
		System.out.println(filePath);
		// Check if the file exists
		if (file.exists())
		{
			
			if (file.isDirectory())
			{
				// Look for default index.html file in a directory
				String location = filePath + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if (!file.exists())
				{
					file = null;
				}
			}
		}
		else
		{
			file = null;
		}

		if (file == null)
		{
			response.setStatus(Protocol.NOT_FOUND_CODE);
			response.setPhrase(Protocol.NOT_FOUND_TEXT);
		}
		else
		{
			response.setFile(file);
			response.setStatus(request.getSuccessCode());
			response.setPhrase(request.getSuccessText());		
		}
		
	}

	@Override
	public boolean doesGet()
	{
		return true;
	}

	@Override
	public void doPut(String serverRootDir, HttpRequest request,
			HttpResponse response) throws Exception
			{
		String filePath = serverRootDir + parseFileName(request.getUri());
		File file = new File(filePath);


		if (file.exists() && file.isDirectory())
		{
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
			return;
		}

		if (file.exists())
		{
			file.delete();
		}

		try
		{
			file.createNewFile();
			try (FileWriter writer = new FileWriter(file))
			{
				writer.write(request.getBody());
			}
		}
		catch (IOException e)
		{
			response.setStatus(Protocol.NOT_SUPPORTED_CODE);
			response.setPhrase(Protocol.NOT_SUPPORTED_TEXT);
			return;
		}

		response.setStatus(request.getSuccessCode());
		response.setPhrase(request.getSuccessText());	}

	@Override
	public boolean doesPut() 
	{
		return true;
	}

	@Override
	public void doDelete(String serverRootDir, HttpRequest request,
			HttpResponse response) throws Exception 
			{
		String filePath = serverRootDir + parseFileName(request.getUri());
		File file = new File(filePath);

		if (!file.exists())
		{
			response.setStatus(request.getSuccessCode());
			response.setPhrase(request.getSuccessText());
			return;
		}

		if (file.isDirectory())
		{
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
			return;
		}
		response.setStatus(request.getSuccessCode());
		response.setPhrase(request.getSuccessText());

		file.delete();
	}

	@Override
	public boolean doesDelete() 
	{
		return true;
	}

	@Override
	public void doPost(String serverRootDir, HttpRequest request,
			HttpResponse response) throws Exception 
			{
		String filePath = serverRootDir + parseFileName(request.getUri());
		File file = new File(filePath);
		
		if (file.exists() && file.isDirectory())
		{
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
			return;
		}

		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			try (FileWriter writer = new FileWriter(file, true))
			{
				writer.write(request.getBody());
			}
		}
		catch (IOException e)
		{
			response.setStatus(Protocol.NOT_SUPPORTED_CODE);
			response.setPhrase(Protocol.NOT_SUPPORTED_TEXT);
			return;
		}

		response.setStatus(request.getSuccessCode());
		response.setPhrase(request.getSuccessText());
		
	}

	@Override
	public boolean doesPost() 
	{
		return true;
	}

	String getServletName()
	{
		return "DefaultServlet";
	}

	String parseFileName(String uri)
	{
		String path = uri;
		if(path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if(!path.contains(getServletName()))
		{
			return path;
		}
		
		path = path.substring(path.indexOf(getServletName()) + getServletName().length());
		return path;
	}
}