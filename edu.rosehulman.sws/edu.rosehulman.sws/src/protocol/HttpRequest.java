package protocol;

import java.io.*;
import java.util.*;

/**
 * Represents a request object for HTTP.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpRequest
{
	private String method;
	private String uri;
	private String version;
	private Map<String, String> header;
	private char[] body;

	public HttpRequest(String method, String uri, String version)
	{
		this.method = method;
		this.uri = uri;
		this.version = version;
		this.header = new HashMap<String, String>();
		this.body = new char[0];
	}

	public String getMethod()
	{
		return this.method;
	}

	public String getUri()
	{
		return this.uri;
	}

	public String getVersion()
	{
		return this.version;
	}

	public Map<String, String> getHeader()
	{
		return Collections.unmodifiableMap(this.header);
	}

	public char[] getBody()
	{
		return this.body;
	}

	/**
	 * Reads raw data from the supplied input stream and constructs a <tt>HttpRequest</tt> object out of the raw data.
	 *
	 * @param inputStream The input stream to read from.
	 * @return A <tt>HttpRequest</tt> object.
	 * @throws Exception Throws either {@link ProtocolException} for bad request or {@link IOException} for socket input stream read errors.
	 */
	public static HttpRequest read(InputStream inputStream) throws Exception
	{
		System.out.println("HttpRequest.read() started...");

		InputStreamReader inStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inStreamReader);

		// First Request Line: GET /somedir/page.html HTTP/1.1
		String line = reader.readLine(); // A line ends with either a \r, or a \n, or both

		if (line == null)
		{
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}

		// We will break this line using space as delimeter into three parts
		StringTokenizer tokenizer = new StringTokenizer(line, " ");

		// Error checking the first line must have exactly three elements
		if (tokenizer.countTokens() != 3)
		{
			throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
		}

		String method = tokenizer.nextToken(); // GET
		String uri = tokenizer.nextToken(); // /somedir/page.html
		String version = tokenizer.nextToken(); // HTTP/1.1
		
		if (!version.equalsIgnoreCase(Protocol.VERSION))
		{
			throw new ProtocolException(Protocol.NOT_SUPPORTED_CODE, Protocol.NOT_SUPPORTED_TEXT);
		}

		HttpRequest request = new HttpRequest(method, uri, version);

		// Rest of the request is a header that maps keys to values
		// e.g. Host: www.rose-hulman.edu
		// We will convert both the strings to lower case to be able to search later
		line = reader.readLine().trim();

		while (!line.equals(""))
		{
			// THIS IS A PATCH
			// Instead of a string tokenizer, we are using string split
			// Lets break the line into two part with first space as a separator

			// First lets trim the line to remove escape characters
			line = line.trim();

			// Now, get index of the first occurrence of space
			int index = line.indexOf(' ');

			if (index > 0 && index < line.length() - 1)
			{
				// Now lets break the string in two parts
				String key = line.substring(0, index); // Get first part, e.g. "Host:"
				String value = line.substring(index + 1); // Get the rest, e.g. "www.rose-hulman.edu"

				// Lets strip off the white spaces from key if any and change it to lower case
				key = key.trim().toLowerCase();

				// Lets also remove ":" from the key
				key = key.substring(0, key.length() - 1);

				// Lets strip white spaces if any from value as well
				value = value.trim();

				// Now lets put the key=>value mapping to the header map
				request.header.put(key, value);
			}

			// Processed one more line, now lets read another header line and loop
			line = reader.readLine().trim();
		}

		int contentLength = 0;
		try
		{
			contentLength = Integer.parseInt(request.header.get(Protocol.CONTENT_LENGTH.toLowerCase()));
		}
		catch (Exception e)
		{
		}

		if (contentLength > 0)
		{
			request.body = new char[contentLength];
			reader.read(request.body);
		}

		return request;
	}

	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------- Header ----------------\n");
		buffer.append(getMethod());
		buffer.append(Protocol.SPACE);
		buffer.append(this.uri);
		buffer.append(Protocol.SPACE);
		buffer.append(this.version);
		buffer.append(Protocol.LF);

		for (Map.Entry<String, String> entry : this.header.entrySet())
		{
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		buffer.append("------------- Body ---------------\n");
		buffer.append(this.body);
		buffer.append("----------------------------------\n");
		return buffer.toString();
	}
}