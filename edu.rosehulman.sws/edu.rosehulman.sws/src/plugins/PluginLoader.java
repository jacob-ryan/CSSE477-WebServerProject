package plugins;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

/**
 * Creates Plugin objects and assigns IServlets
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PluginLoader
{
	public static Plugin loadPlugin(String name) throws Exception
	{
		String path = PluginRegistry.pluginLocation + name;
		try (JarFile jarFile = new JarFile(path))
		{
			JarEntry configFile = null;
			List<JarEntry> classFiles = new ArrayList<JarEntry>();

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory())
				{
					if (entry.getName().toLowerCase().equals("configuration.txt"))
					{
						configFile = entry;
					}
					if (entry.getName().endsWith(".class"))
					{
						classFiles.add(entry);
					}
				}
			}

			if (configFile == null)
			{
				throw new Exception("Plugin JAR does not contain a configuration file!");
			}
			if (classFiles.size() < 1)
			{
				throw new Exception("Plugin JAR does not contain at least one class!");
			}

			String jarPath = "jar:file:" + path + "!/";
			// Hack to allow plugins to load their own
			// helper classes (inside their JAR file).
			addLibrary(new File(path));

			URL[] urls = new URL[] { new URL(jarPath) };
			try (URLClassLoader classLoader = new URLClassLoader(urls))
			{
				Plugin plugin = new Plugin();

				URL configURL = new URL(jarPath + configFile.getName());
				InputStream configStream = configURL.openStream();
				BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream));

				String rootUrl = configReader.readLine();
				plugin.setRootUrl(rootUrl);

				while (true)
				{
					String line = configReader.readLine();
					if (line == null)
					{
						break;
					}
					else
					{
						String[] parts = line.split(Pattern.quote("\t"));
						if (parts.length != 3)
						{
							throw new Exception("Configuration file contains invalid line: " + line);
						}

						String method = parts[0];
						String uri = parts[1];
						String className = parts[2];
						System.out.println("[PluginLoader] Loading class: " + className);
						Class<?> clazz = classLoader.loadClass(className);
						IServlet servlet = (IServlet) clazz.newInstance();
						plugin.addServlet(new ServletMapping(method, uri), servlet);
					}
				}

				return plugin;
			}
		}
	}
	
	private static void addLibrary(File file) throws Exception
	{
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file.toURI().toURL() });
	}
}