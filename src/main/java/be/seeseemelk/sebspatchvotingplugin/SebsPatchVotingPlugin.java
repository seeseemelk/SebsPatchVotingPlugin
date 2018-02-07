package be.seeseemelk.sebspatchvotingplugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class SebsPatchVotingPlugin extends JavaPlugin implements Listener
{
	private VoteInventory voteInventory;
	private IconInventory iconInventory;
	private Collection<Player> removing = new HashSet<>();
	private boolean opened = false;
	
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
		iconInventory = new IconInventory();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	/**
	 * Set whether the voting should be enabled or disabled.
	 * @param opened {@code true} to enable voting, {@code false} to disable it.
	 */
	public void setOpened(boolean opened)
	{
		this.opened = opened;
	}
	
	/**
	 * Gets the vote inventory.
	 * @return The vote inventory.
	 */
	public VoteInventory getVoteInventory()
	{
		return voteInventory;
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
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				showVoteInventory(player);
				return true;
			}
			else if (args.length >= 2)
			{
				String action = args[0];
				String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				if (action.equals("add"))
				{
					player.setMetadata("sebspatchvoting_optionname", new FixedMetadataValue(this, name));
					player.openInventory(iconInventory.getInventory());
					return true;
				}
				else
				{
					return false;
				}
			}
			else if (args.length == 1)
			{
				String action = args[0].toLowerCase();
				switch (action)
				{
					case "remove":
						removing.add(player);
						player.openInventory(voteInventory.getInventory());
						return true;
					case "open":
						opened = true;
						Bukkit.broadcastMessage(Messages.MSG_OPENED);
						return true;
					case "close":
						opened = false;
						Bukkit.broadcastMessage(Messages.MSG_CLOSED);
						return true;
					default:
						return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			sender.sendMessage(Messages.ERR_ONLY_PLAYER);
			return true;
		}
	}
	
	@EventHandler
	public void onInventoryClicked(InventoryClickEvent event)
	{
		if (event.isCancelled())
			return;
		else if (event.getInventory().equals(voteInventory.getInventory()))
		{
			if (voteInventory.getInventory().equals(event.getClickedInventory()))
			{
				ItemStack item = event.getCurrentItem();
				if (item.getType() != Material.AIR)
				{
					Player player = (Player) event.getWhoClicked();
					String optionName = item.getItemMeta().getDisplayName();
					if (removing.contains(player))
					{
						voteInventory.removeVoteOption(optionName);
						player.closeInventory();
						removing.remove(player);
						player.sendMessage(Messages.MSG_OPTION_REMOVED);
					}
					else
					{
						if (opened)
						{
							if (voteInventory.hasVotedOn(player, optionName))
								voteInventory.removeVote(player, optionName);
							else
								voteInventory.addVote(player, optionName);
						}
						else
						{
							player.sendMessage(Messages.ERR_NOT_OPENED);
						}
					}
				}
			}
			event.setCancelled(true);
		}
		else if (event.getInventory().equals(iconInventory.getInventory()))
		{
			if (iconInventory.getInventory().equals(event.getClickedInventory()))
			{
				HumanEntity entity = event.getWhoClicked();
				ItemStack item = event.getCurrentItem();
				String optionName = entity.getMetadata("sebspatchvoting_optionname").get(0).asString();
				voteInventory.addVoteOption(optionName, item.getType());
				entity.closeInventory();
				entity.sendMessage(Messages.MSG_OPTION_ADDED);
			}
			event.setCancelled(true);
		}
	}
}
































