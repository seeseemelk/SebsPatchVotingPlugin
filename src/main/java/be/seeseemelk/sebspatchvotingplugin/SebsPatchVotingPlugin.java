package be.seeseemelk.sebspatchvotingplugin;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import net.md_5.bungee.api.ChatColor;

public class SebsPatchVotingPlugin extends JavaPlugin implements Listener
{
	private VoteInventory voteInventory;
	
	public SebsPatchVotingPlugin()
	{
		super();
	}
	
	protected SebsPatchVotingPlugin(final JavaPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file)
	{
		super(loader, description, dataFolder, file);
	}
	
	@Override
	public void onEnable()
	{
		voteInventory = new VoteInventory();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	/**
	 * Shows the voting inventory to a player.
	 * @param player The player to whom the voting inventory should be shown.
	 */
	public void showVoteInventory(Player player)
	{
		player.openInventory(voteInventory.getInventory());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			if (sender instanceof Player)
			{
				Player player = (Player) sender;
				showVoteInventory(player);
				return true;
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Only players can use this command");
			}
		}
		return false;
	}
}
































