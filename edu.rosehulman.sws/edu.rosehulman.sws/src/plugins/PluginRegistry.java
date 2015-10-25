package plugins;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PluginRegistry
{
	public static final String pluginLocation = "./plugins/";

	private HashSet<String> installedPlugins;
	private Path registryPath;

	public PluginRegistry() throws IOException
	{
		this.installedPlugins = new HashSet<String>();

		Path folder = Paths.get(PluginRegistry.pluginLocation);
		if (!Files.exists(folder))
		{
			Files.createDirectory(folder);
		}

		this.registryPath = Paths.get(PluginRegistry.pluginLocation, "registry.txt");
		if (!Files.exists(this.registryPath))
		{
			Files.createFile(this.registryPath);
		}

		readRegistry();

		System.out.println("[PluginRegistry] Successfully loaded plugin registry.");
	}

	public boolean isPluginInstalled(String name)
	{
		return this.installedPlugins.contains(name);
	}

	public void addInstalledPlugin(String name)
	{
		this.installedPlugins.add(name);
	}

	public void saveRegistry() throws IOException
	{
		try (FileWriter fileWriter = new FileWriter(this.registryPath.toString()))
		{
			try (BufferedWriter writer = new BufferedWriter(fileWriter))
			{
				for (String pluginName : this.installedPlugins)
				{
					writer.write(pluginName + "\n");
				}
			}
		}
	}

	private void readRegistry() throws IOException
	{
		try (FileReader fileReader = new FileReader(this.registryPath.toString()))
		{
			try (BufferedReader reader = new BufferedReader(fileReader))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					this.installedPlugins.add(line);
				}
			}
		}
	}
}