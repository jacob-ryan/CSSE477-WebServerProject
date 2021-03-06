package plugins;

import java.io.*;
import java.nio.file.*;

import javax.swing.*;

/**
 * Listens for new/modified plugins in the folder
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PluginWatcher
{
	public PluginWatcher()
	{
		loadExistingPlugins();
		runDirectoryWatcher();
	}

	private void loadExistingPlugins()
	{
		log("[PluginWatcher] Loading all existing plugin files...");
		long startTime = System.nanoTime();

		int count = 0;
		File folder = new File(PluginRegistry.pluginLocation);
		File[] files = folder.listFiles();
		for (File file : files)
		{
			if (tryLoadFile(file))
			{
				count += 1;
			}
		}

		long elapsedTime = System.nanoTime() - startTime;
		log("[PluginWatcher] Loaded " + count + " plugins in " + elapsedTime / 1000000 + " ms.");
	}

	private void runDirectoryWatcher()
	{
		try
		{
			Path path = Paths.get(PluginRegistry.pluginLocation);
			WatchService watchService = FileSystems.getDefault().newWatchService();
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_DELETE);
			log("[PluginWatcher] Successfully created directory WatchService.");

			while (true)
			{
				try
				{
					processEvents(watchService);
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			log("[PluginWatcher] Error creating directory WatchService: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Error creating directory WatchService:\n" + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private boolean processEvents(WatchService watchService) throws InterruptedException
	{
		WatchKey key = watchService.take();
		for (WatchEvent<?> event : key.pollEvents())
		{
			if (event.kind() == StandardWatchEventKinds.OVERFLOW)
			{
				log("[PluginWatcher] WatchService OVERFLOW occured.");
				continue;
			}
			if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
			{
				log("[PluginWatcher] Loading plugin...");
				@SuppressWarnings("unchecked")
				File file = ((WatchEvent<Path>) event).context().toFile();
				tryLoadFile(file);
			}
			if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
			{
				int choice = JOptionPane.showConfirmDialog(null, "This plugin has already been installed.\n"
						+ "Do you want to reload?\n");
				if (choice == JOptionPane.YES_OPTION)
				{
					@SuppressWarnings("unchecked")
					File file = ((WatchEvent<Path>) event).context().toFile();
					tryLoadFile(file);
				}
			}
		}
		return key.reset();
	    
	}

	private boolean tryLoadFile(File file)
	{
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".jar"))
		{
			PluginManager.instance.loadPlugin(fileName);
			return true;
		}
		return false;
	}

	private void log(String status)
	{
		System.out.println(status);
	}
}