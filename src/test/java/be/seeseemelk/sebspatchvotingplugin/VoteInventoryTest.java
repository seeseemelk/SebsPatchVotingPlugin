package be.seeseemelk.sebspatchvotingplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bukkit.Material;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteAlreadyPlacedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteLimitReachedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VoteNotPlacedException;
import be.seeseemelk.sebspatchvotingplugin.exceptions.VotingOptionNotFoundException;

public class VoteInventoryTest
{
	@SuppressWarnings("unused")
	private ServerMock server;
	private VoteInventory inventory;
	private PlayerMock player;

	@Before
	public void setUp() throws Exception
	{
		server = MockBukkit.mock();
		player = server.addPlayer();
		inventory = new VoteInventory();
	}
	
	@After
	public void tearDown()
	{
		MockBukkit.unload();
	}

	@Test
	public void hasItem_EmptyInventory_False()
	{
		assertFalse(inventory.hasVoteOption("option"));
	}
	
	@Test
	public void addItem_EmptyInventory_AddsItem()
	{
		inventory.addVoteOption("option", Material.WOOD);
		assertTrue(inventory.hasVoteOption("option"));
	}
	
	@Test(expected = IllegalAccessError.class)
	public void addItem_AddsItemTwice_ThrowsException()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVoteOption("option", Material.STONE);
	}
	
	@Test
	public void removeItem_TwoItems_RemovesOneItem()
	{
		inventory.addVoteOption("option a", Material.WOOD);
		inventory.addVoteOption("option b", Material.WOOD);
		inventory.removeVoteOption("option a");
		assertFalse(inventory.hasVoteOption("option a"));
		assertTrue(inventory.hasVoteOption("option b"));
	}
	
	@Test(expected = VotingOptionNotFoundException.class)
	public void removeItem_ItemDoesNotExist_ThrowsException()
	{
		inventory.removeVoteOption("option");
	}
	
	@Test
	public void getMaximumNumberOfVotes_Default_ThreeVotes()
	{
		assertEquals(3, inventory.getMaximumNumberOfVotes());
	}
	
	@Test
	public void setMaximumNumberOfVotes_NumberOfVotesChanged_NewNumber()
	{
		inventory.setMaximumNumberOfVotes(6);
		assertEquals(6, inventory.getMaximumNumberOfVotes());
	}
	
	@Test
	public void getVotesOnOption_EmptyOption_NoVotes()
	{
		inventory.addVoteOption("option", Material.WOOD);
		assertEquals(0, inventory.getVotesOnOption("option"));
	}
	
	@Test(expected = VotingOptionNotFoundException.class)
	public void getVotesOnOption_OptionDoesNotExist_ThrowsException()
	{
		assertEquals(0, inventory.getVotesOnOption("option"));
	}
	
	@Test
	public void addVote_EmptyOption_OneVote()
	{
		inventory.setMaximumNumberOfVotes(1);
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		assertEquals(1, inventory.getVotesOnOption("option"));
	}
	
	@Test(expected = VotingOptionNotFoundException.class)
	public void addVote_OptionDoesNotExist_ThrowsException()
	{
		inventory.addVote(player, "option");
	}
	
	@Test(expected = VoteAlreadyPlacedException.class)
	public void addVote_AlreadyVotedOnIt_ThrowsException()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		inventory.addVote(player, "option");
	}
	
	@Test(expected = VoteLimitReachedException.class)
	public void addVote_LimitReached_ThrowsException()
	{
		inventory.setMaximumNumberOfVotes(1);
		inventory.addVoteOption("option a", Material.WOOD);
		inventory.addVoteOption("option b", Material.WOOD);
		inventory.addVote(player, "option a");
		inventory.addVote(player, "option b");
	}
	
	@Test
	public void removeVote_HadVoted_NoVotes()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		inventory.removeVote(player, "option");
		assertEquals(0, inventory.getVotesOnOption("option"));
		assertEquals(0, inventory.getNumberOfVotesByPlayer(player));
	}
	
	@Test(expected = VoteNotPlacedException.class)
	public void removeVote_PlayerDidNotVote_ThrowsException()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.removeVote(player, "option");
	}
	
	@Test(expected = VotingOptionNotFoundException.class)
	public void removeVote_OptionDoesNotExist_ThrowsException()
	{
		inventory.removeVote(player, "option");
	}
	
	@Test
	public void getNumberOfVotesByPlayer_PlayerNeverVoted_Zero()
	{
		assertEquals(0, inventory.getNumberOfVotesByPlayer(player));
	}
	
	@Test
	public void getNumberOfVotesByPlayer_PlayerVotedOnce_One()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		assertEquals(1, inventory.getNumberOfVotesByPlayer(player));
	}
	
	@Test
	public void hasVotedOn_PlayerDidNotVote_False()
	{
		inventory.addVoteOption("option", Material.WOOD);
		assertFalse(inventory.hasVotedOn(player, "option"));
	}
	
	@Test
	public void hasVotedOn_PlayerDidVote_True()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		assertTrue(inventory.hasVotedOn(player, "option"));
	}
	
	@Test(expected = VotingOptionNotFoundException.class)
	public void hasVotedOn_OptionDoesNotExist_ThrowsException()
	{
		inventory.addVote(player, "option");
	}
	
	@Test
	public void canVoteOn_PlayerDidNotVote_True()
	{
		inventory.addVoteOption("option", Material.WOOD);
		assertTrue(inventory.canVoteOn(player, "option"));
	}
	
	@Test
	public void canVoteOn_PlayerAlreadyVoted_False()
	{
		inventory.addVoteOption("option", Material.WOOD);
		inventory.addVote(player, "option");
		assertFalse(inventory.canVoteOn(player, "option"));
	}
	
	@Test
	public void canVoteOn_VotingOptionDoesNotExist_False()
	{
		assertTrue(inventory.canVoteOn(player, "option"));
	}
	
	@Test
	public void getInventory_NotNull()
	{
		assertNotNull(inventory.getInventory());
	}
	
}




























