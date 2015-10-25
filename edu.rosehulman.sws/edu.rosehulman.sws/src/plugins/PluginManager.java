package plugins;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class PluginManager extends Thread
{
	public static PluginManager instance;

	private Map<String, Plugin> plugins;
	private final Object lock;
	private PluginRegistry registry;

	public PluginManager()
	{
		this.plugins = new HashMap<String, Plugin>();
		this.lock = new Object();

		start();

		PluginManager.instance = this;
	}

	public Plugin getPlugin(String rootUrl)
	{
		return this.plugins.get(rootUrl);
	}

	public void loadPlugin(String name)
	{
		boolean load = false;
		if (this.registry.isPluginInstalled(name))
		{
			load = true;
		}
		else
		{
			int choice = JOptionPane.showConfirmDialog(null, "Do you want to load the following plugin?\n" + name);
			if (choice == JOptionPane.YES_OPTION)
			{
				load = true;
			}
		}
		if (load)
		{
			try
			{
				long startTime = System.nanoTime();

				synchronized (this.lock)
				{
					Plugin plugin = PluginLoader.loadPlugin(name);
					this.registry.addInstalledPlugin(name);

					// TODO: Check for overwrites/existing plugins.
					this.plugins.put(plugin.getRootUrl(), plugin);
					plugin.start();
				}

				long elapsedTime = System.nanoTime() - startTime;
				log("[PluginManager] Loaded plugin " + name + " in " + elapsedTime / 1000000 + " ms.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				log("[PluginManager] Error loading plugin " + name + ": " + e.getMessage());
				JOptionPane.showMessageDialog(null, "Error loading plugin " + name + ":\n" + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
		else
		{
			log("[PluginManager] Did not load plugin: " + name);
		}
	}

	public void stopAllPlugins()
	{
		log("[PluginManager] Stopping all plugins...");

		synchronized (this.lock)
		{
			long startTime = System.nanoTime();
			for (Plugin plugin : this.plugins.values())
			{
				plugin.stop();
			}
			long elapsedTime = System.nanoTime() - startTime;
			log("[PluginManager] Stopped plugins in " + elapsedTime / 1000000 + " ms.");

			if (this.registry != null)
			{
				try
				{
					this.registry.saveRegistry();
					log("[PluginManager] Saved plugin registry.");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					log("[PluginManager] Error saving plugin registry: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			this.registry = new PluginRegistry();
			new PluginWatcher();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading plugin registry:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void log(String status)
	{
		System.out.println(status);
	}
}