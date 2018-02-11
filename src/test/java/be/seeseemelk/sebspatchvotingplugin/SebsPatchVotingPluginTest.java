package be.seeseemelk.sebspatchvotingplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.SimpleInventoryMock;

public class SebsPatchVotingPluginTest
{
	private ServerMock server;
	private SebsPatchVotingPlugin plugin;

	@Before
	public void setUp() throws Exception
	{
		server = MockBukkit.mock();
		plugin = MockBukkit.load(SebsPatchVotingPlugin.class);
	}

	@After
	public void tearDown()
	{
		MockBukkit.unload();
	}

	@Test
	public void getVoteInventory_GetsInventory()
	{
		assertNotNull(plugin.getVoteInventory());
	}

	@Test
	public void inventoryClickEvent_VotingInventory_CancelledAndVoteAdded()
	{
		plugin.setOpened(true);
		Player player = server.addPlayer();
		plugin.getVoteInventory().addVoteOption("option", Material.APPLE);
		
		plugin.showVoteInventory(player);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
				ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
		assertEquals(1, plugin.getVoteInventory().getNumberOfVotesByPlayer(player));
		assertEquals(1, plugin.getVoteInventory().getVotesOnOption("option"));
	}
	
	@Test
	public void inventoryClickEvent_VotingInventoryAlreadyVoted_CancelledAndVoteRemoved()
	{
		plugin.setOpened(true);
		Player player = server.addPlayer();
		plugin.getVoteInventory().addVoteOption("option", Material.APPLE);
		plugin.getVoteInventory().addVote(player, "option");
		
		plugin.showVoteInventory(player);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
				ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
		assertEquals(0, plugin.getVoteInventory().getNumberOfVotesByPlayer(player));
		assertEquals(0, plugin.getVoteInventory().getVotesOnOption("option"));
	}
	
	@Test
	public void inventoryClickEvent_VotingInventoryTopButNoItem_Cancelled()
	{
		Player player = server.addPlayer();
		plugin.getVoteInventory().addVoteOption("option", Material.APPLE);
		plugin.showVoteInventory(player);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 1,
				ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
	}
	
	@Test
	public void inventoryClickEvent_OtherInventory_NotCancelled()
	{
		Inventory inventory = new SimpleInventoryMock();
		Player player = server.addPlayer();
		player.openInventory(inventory);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
				ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertFalse(event.isCancelled());
	}
	
	@Test
	public void inventoryClickEvent_OtherInventoryButCancelled_StillCancelled()
	{
		Inventory inventory = new SimpleInventoryMock();
		Player player = server.addPlayer();
		player.openInventory(inventory);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
				ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		event.setCancelled(true);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
	}
	
	@Test
	public void commandPvote_UnknownArg_False()
	{
		Player player = server.addPlayer();
		assertFalse(player.performCommand("pvote random command"));
	}
	
	@Test
	public void commandPvote_PlayerNoArgs_OpensInventory()
	{
		plugin.setOpened(true);
		PlayerMock player = server.addPlayer();
		assertTrue(player.performCommand("pvote"));
		assertEquals(plugin.getVoteInventory().getInventory(), player.getOpenInventory().getTopInventory());
		player.assertNoMoreSaid();
	}
	
	@Test
	public void commandPvote_Console_Error()
	{
		ConsoleCommandSenderMock console = (ConsoleCommandSenderMock) server.getConsoleSender();
		assertTrue(server.dispatchCommand(console, "pvote"));
		console.assertSaid(Messages.getString("ERR.ONLY_PLAYER"));
		console.assertNoMoreSaid();
	}
	
	@Test
	public void commandPvoteAdd_Player_OptionAdded()
	{
		PlayerMock player = server.addPlayer();
		player.performCommand("pvote add my option");
		InventoryView view = player.getOpenInventory();
		assertEquals(InventoryType.CHEST, view.getType());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
		assertEquals(InventoryType.CRAFTING, player.getOpenInventory().getType());
		assertTrue(plugin.getVoteInventory().hasVoteOption("my option"));
		player.assertSaid(Messages.getString("MSG.OPTION_ADDED"));
	}
	
	@Test
	public void commandPvoteRemove_OptionAdded_OptionRemoved()
	{
		PlayerMock player = server.addPlayer();
		plugin.getVoteInventory().addVoteOption("option", Material.WOOD);
		
		player.performCommand("pvote remove");
		InventoryView view = player.getOpenInventory();
		assertEquals(InventoryType.CHEST, view.getType());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);
		server.getPluginManager().callEvent(event);
		assertTrue(event.isCancelled());
		assertEquals(InventoryType.CRAFTING, player.getOpenInventory().getType());
		assertFalse(plugin.getVoteInventory().hasVoteOption("my option"));
		player.assertSaid(Messages.getString("MSG_OPTION_REMOVED"));
	}
	
}


































