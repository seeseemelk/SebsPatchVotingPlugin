package be.seeseemelk.sebspatchvotingplugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteAlreadyPlacedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteLimitReachedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteNotPlacedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VotingOptionNotFoundException;

/**
 * A {@code VoteInventory} is an inventory that can contain several voting items.
 * Note that the name of each voting option is case-insensitive.
 */
public class VoteInventory
{
	private Inventory inventory;
	private Map<String, ItemStack> votingItems = new HashMap<>();
	private int maxVotes = 3;
	private Map<Player, Collection<String>> votes = new HashMap<>();

	public VoteInventory()
	{
		inventory = Bukkit.createInventory(null, 9*3, "Patch Voting");
	}
	
	/**
	 * Gets the inventory to be used by bukkit.
	 * @return A Inventory object that can be used by bukkit.
	 */
	public Inventory getInventory()
	{
		return inventory;
	}
	
	/**
	 * Gets the maximum number of votes a player is allowed to place.
	 * @return The maximum number of votes a player can make.
	 */
	public int getMaximumNumberOfVotes()
	{
		return maxVotes;
	}
	
	/**
	 * Sets the maximum number of votes a player is allowed to place.
	 * @param maxVotes The maximum number of votes a player can make.
	 */
	public void setMaximumNumberOfVotes(int maxVotes)
	{
		this.maxVotes = maxVotes;
	}
	
	/**
	 * Checks if the inventory has a certain voting option.
	 * @param name The name of the voting option the inventory should have.
	 * @return {@code true} if the inventory has that option, {@code false} if he doesn't.
	 */
	public boolean hasVoteOption(String name)
	{
		return votingItems.containsKey(name);
	}

	/**
	 * Adds a vote option to this {@code VoteInventory}
	 * @param name The name of the option.
	 * @param type The material type of the item.
	 * @throws IllegalAccessError when the vote option already exists.
	 */
	public void addVoteOption(String name, Material type) throws IllegalAccessError
	{
		if (hasVoteOption(name))
			throw new IllegalAccessError("Vote option was already added");
		
		ItemStack item = new ItemStack(type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList("Votes: 0"));
		item.setItemMeta(meta);
		inventory.addItem(item);
		votingItems.put(name, item);
	}
	
	/**
	 * Removes a voting option from the inventory.
	 * @param name The name of the voting option to remove.
	 */
	public void removeVoteOption(String name)
	{
		if (hasVoteOption(name))
		{
			List<ItemStack> items = Arrays.asList(inventory.getContents());
			items = items.stream().filter(item -> !itemRepresentsVote(item, name)).collect(Collectors.toList());
			inventory.setContents(items.toArray(new ItemStack[0]));
			votingItems.remove(name);
		}
		else
		{
			throw new IllegalAccessError("Vote option does not exist");
		}
	}
	
	/**
	 * Updates the item that represents a specific option and increases the number of votes it has.
	 * @param option The voting option whose item should be updated.
	 * @param votesToAdd The number of votes to add to the item. Can be negative for subtraction.
	 */
	private void updateItemAddVote(String option, int votesToAdd)
	{
		ItemStack item = votingItems.get(option);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		String votesStr = lore.get(0);
		int votes = Integer.parseInt(votesStr.substring(7));
		votes += votesToAdd;
		lore.set(0, "Votes: " + votes);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	/**
	 * Adds a vote from a player to a specific option.
	 * @param player The player that added the vote.
	 * @param option The option to add the vote to.
	 */
	public void addVote(Player player, String option)
	{
		if (hasVoteOption(option))
		{
			Collection<String> votes = getVotesByPlayer(player);
			if (votes.size() >= maxVotes)
			{
				throw new VoteLimitReachedException(player.getName() + " reached his voting limit");
			}
			else if (votes.contains(option))
			{
				throw new VoteAlreadyPlacedException(player.getName() + " already voted on " + option);
			}
			else
			{
				votes.add(option);
				updateItemAddVote(option, 1);
			}
		}
		else
		{
			throw new VotingOptionNotFoundException("Voting option " + option + " does not exist");
		}
	}
	
	/**
	 * Removes a player's vote from an option.
	 * @param player The player whose vote should be removed.
	 * @param option The option from which it should be removed.
	 */
	public void removeVote(Player player, String option)
	{
		if (hasVoteOption(option))
		{
			Collection<String> votes = getVotesByPlayer(player);
			if (votes.contains(option))
			{
				votes.remove(option);
				updateItemAddVote(option, -1);
			}
			else
			{
				throw new VoteNotPlacedException("Player " + player.getName() + " did not vote on " + option);
			}
		}
		else
		{
			throw new VotingOptionNotFoundException("Voting option " + option + " does not exist");
		}
	}
	
	/**
	 * Gets the number of votes for an option.
	 * @param option The option to check for.
	 * @return The number of votes on it.
	 */
	public int getVotesOnOption(String option)
	{
		ItemStack item = votingItems.get(option);
		if (item != null)
		{
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			String votesStr = lore.get(0);
			int votes = Integer.parseInt(votesStr.substring(7));
			return votes;
		}
		else
		{
			throw new VotingOptionNotFoundException("Voting option " + option + " does not exist");
		}
	}
	
	/**
	 * Gets the number of votes a player made in total.
	 * @param player The player to check.
	 * @return
	 */
	public int getNumberOfVotesByPlayer(Player player)
	{
		if (votes.containsKey(player))
			return votes.get(player).size();
		else
			return 0;
	}
	
	/**
	 * Gets all the votes a player made.
	 * @param player The player to check for.
	 * @return A collection of votes a player made.
	 */
	public Collection<String> getVotesByPlayer(Player player)
	{
		if (votes.containsKey(player))
		{
			return votes.get(player);
		}
		else
		{
			Collection<String> playerVotes = new LinkedList<>();
			votes.put(player, playerVotes);
			return playerVotes;
		}
	}
	
	/**
	 * Checks if a player has voted on an option.
	 * @param player The player to check for.
	 * @param option The option to check for.
	 * @return {@code true} if the player has voted on it, {@code false} if the player hasn't.
	 */
	public boolean hasVotedOn(Player player, String option)
	{
		Collection<String> votes = getVotesByPlayer(player);
		return votes.contains(option);
	}
	
	/**
	 * Checks if a player can vote on a specific option.
	 * @param player The player to check for.
	 * @param option The option to check for.
	 * @return {@code true} if the player can vote on the option, {@code false} if the player can't for any reason.
	 */
	public boolean canVoteOn(Player player, String option)
	{
		if (getNumberOfVotesByPlayer(player) >= maxVotes)
			return false;
		else
			return !hasVotedOn(player, option);
	}

	/**
	 * Checks if the voting item represents a specific vote option.
	 * @param item The voting item to check. If this is {@code null} it will immediately return false.
	 * @param voteOption The name of the voting option to check against.
	 * @return {@code true} if the item represents the vote option, {@code false} if it doesn't.
	 */
	private static final boolean itemRepresentsVote(ItemStack item, String voteOption)
	{
		if (item == null)
			return false;
		else
			return item.getItemMeta().getDisplayName().equalsIgnoreCase(voteOption);
			
	}

}
