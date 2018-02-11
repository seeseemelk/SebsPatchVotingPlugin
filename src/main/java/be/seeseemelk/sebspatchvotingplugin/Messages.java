package be.seeseemelk.sebspatchvotingplugin;

import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Messages
{
	private static YamlConfiguration messages;
	
	public static void setLanguage(Plugin plugin, String languageCode)
	{
		Reader reader = new InputStreamReader(plugin.getResource("messages_" + languageCode + ".yml"));
		messages = YamlConfiguration.loadConfiguration(reader);
	}
	
	public static String getString(String key)
	{
		if (messages.contains(key))
			return messages.getString(key);
		else
			throw new IllegalArgumentException("Could not find message with key " + key);
	}
}
