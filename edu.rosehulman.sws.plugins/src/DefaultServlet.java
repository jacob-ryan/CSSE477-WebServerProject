import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import plugins.IServlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;

public class DefaultServlet implements IServlet
{
	private static final String NAME = "DefaultServlet";
	
	Map<String, byte[]> cachedFiles;

	@Override
	public void start()
	{
		cachedFiles = new HashMap<String, byte[]>();
		System.out.println("DefaultPlugin is starting...");
		try
		{
			Thread.sleep(100);
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
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		String method = request.getMethod();
		try
		{
			if (method.equalsIgnoreCase("GET"))
			{
				doGet(rootDirectory, request, response);
			}
			else if (method.equalsIgnoreCase("POST"))
			{
				doPost(rootDirectory, request, response);
			}
			else if (method.equalsIgnoreCase("PUT"))
			{
				doPut(rootDirectory, request, response);
			}
			else if (method.equalsIgnoreCase("DELETE"))
			{
				doDelete(rootDirectory, request, response);
			}
			else
			{
				throw new Exception();
			}
		}
		catch (Exception e)
		{
			response.setStatus(Protocol.NOT_SUPPORTED_CODE);
			response.setPhrase(Protocol.NOT_SUPPORTED_TEXT);
		}
	}
	
	private void setBody(HttpResponse response, File file) throws IOException 
	{
		if(cachedFiles.containsKey(file.getName()))
		{
			response.setBody(cachedFiles.get(file.getName()));
		}
		else
		{
			Path path = Paths.get(file.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);
			cachedFiles.put(file.getName(), data);
			response.setBody(data);
		}
		
	}

	private void addHeaders(HttpResponse response, File file)
	{
		// Lets add last modified date for the file
		long timeSinceEpoch = file.lastModified();
		Date modifiedTime = new Date(timeSinceEpoch);
		response.put(Protocol.LAST_MODIFIED, modifiedTime.toString());

		// Lets get content length in bytes
		long length = file.length();
		response.put(Protocol.CONTENT_LENGTH, length + "");

		// Lets get MIME type for the file
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mime = fileNameMap.getContentTypeFor(file.getName());
		// The fileNameMap cannot find mime type for all of the documents, e.g. doc, odt, etc.
		// So we will not add this field if we cannot figure out what a mime type is for the file.
		// Let browser do this job by itself.
		if (mime != null)
		{
			response.put(Protocol.CONTENT_TYPE, mime);
		}
	}
	
	private void invalidateFile(File file)
	{
		if(cachedFiles.containsKey(file.getName()))
		{
			cachedFiles.remove(file.getName());
		}
	}

	public void doGet(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception
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
			this.addHeaders(response, file);
			this.setBody(response, file);
			response.setStatus(request.getSuccessCode());
			response.setPhrase(request.getSuccessText());
		}

	}
	
	

	public void doPut(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception
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
		response.setPhrase(request.getSuccessText());
		this.invalidateFile(file);
	}

	public void doDelete(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception
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
		this.invalidateFile(file);
		file.delete();
	}

	public void doPost(String serverRootDir, HttpRequest request, HttpResponse response) throws Exception
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
		this.invalidateFile(file);
	}

	private String parseFileName(String uri)
	{
		String path = uri;
		if (path.endsWith("/"))
		{
			path = path.substring(0, path.length() - 1);
		}
		if (!path.contains(DefaultServlet.NAME))
		{
			return path;
		}

		return path.substring(path.indexOf(DefaultServlet.NAME) + DefaultServlet.NAME.length());
	}
}