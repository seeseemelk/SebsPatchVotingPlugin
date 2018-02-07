package be.seeseemelk.sebspatchvotingplugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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
				if (player.hasPermission("pvote.basic"))
				{
					showVoteInventory(player);
					return true;
				}
				else
				{
					player.sendMessage(Messages.ERR_BASIC_PERMISSION);
					return true;
				}
			}
			else if (args.length >= 2)
			{
				String action = args[0];
				String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				if (action.equals("add"))
				{
					if (player.hasPermission("pvote.admin"))
					{
						player.setMetadata("sebspatchvoting_optionname", new FixedMetadataValue(this, name));
						player.openInventory(iconInventory.getInventory());
						return true;
					}
					else
					{
						player.sendMessage(Messages.ERR_ADMIN_PERMISSION);
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			else if (args.length == 1)
			{
				if (player.hasPermission("pvote.admin"))
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
					player.sendMessage(Messages.ERR_ADMIN_PERMISSION);
					return true;
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
	
	/**
	 * Executed when a player tries to remove an option from the vote inventory by clicking on the item.
	 * @param player The player that is removing an item.
	 * @param item The item that should be removed.
	 */
	private void voteInventoryRemoveOption(Player player, ItemStack item)
	{
		voteInventory.removeVoteOption(item.getItemMeta().getDisplayName());
		player.closeInventory();
		removing.remove(player);
		player.sendMessage(Messages.MSG_OPTION_REMOVED);
	}
	
	/**
	 * Executed when a player tries to vote on an option in the vote inventory by clicking on the item.
	 * @param player The player that is placing the vote.
	 * @param item The item that the player voted for.
	 */
	private void voteInventoryPlaceVote(Player player, ItemStack item)
	{
		String option = item.getItemMeta().getDisplayName();
		if (voteInventory.hasVotedOn(player, option))
			voteInventory.removeVote(player, option);
		else
			voteInventory.addVote(player, option);
	}
	
	/**
	 * Executed when a vote inventory was clicked by a player.
	 * @param player The player that clicked on the inventory.
	 * @param item The item that was clicked on by the player.
	 */
	private void onVoteInventoryClicked(Player player, ItemStack item)
	{
		if (removing.contains(player))
			voteInventoryRemoveOption(player, item);
		else if (opened)
			voteInventoryPlaceVote(player, item);
		else
			player.sendMessage(Messages.ERR_NOT_OPENED);
	}
	
	/**
	 * Executed when an icon inventory was clicked by a player.
	 * @param player The player that clicked on the inventory.
	 * @param item The item that was clicked on by the player.
	 */
	private void onIconInventoryClicked(Player player, ItemStack item)
	{
		String optionName = player.getMetadata("sebspatchvoting_optionname").get(0).asString();
		voteInventory.addVoteOption(optionName, item.getType());
		player.closeInventory();
		player.sendMessage(Messages.MSG_OPTION_ADDED);
	}
	
	@EventHandler
	public void onInventoryClicked(InventoryClickEvent event)
	{
		if (event.isCancelled())
			return;
		
		Inventory clickedInventory = event.getClickedInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		Inventory inventory = event.getInventory();
		if (inventory.equals(voteInventory.getInventory()) || inventory.equals(iconInventory.getInventory()))
		{
			event.setCancelled(true);
			if (item != null && item.getType() != Material.AIR)
			{
				if (voteInventory.getInventory().equals(clickedInventory))
					onVoteInventoryClicked(player, item);
				else if (iconInventory.getInventory().equals(clickedInventory))
					onIconInventoryClicked(player, item);
			}
		}
	}
}
































