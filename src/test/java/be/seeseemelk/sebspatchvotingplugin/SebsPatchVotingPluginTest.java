package be.seeseemelk.sebspatchvotingplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
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
	public void inventoryClickEvent_VotingInventory_Cancelled()
	{
		Player player = server.addPlayer();
		plugin.showVoteInventory(player);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
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
		Player player = server.addPlayer();
		assertTrue(player.performCommand("pvote"));
		assertEquals(plugin.getVoteInventory().getInventory(), player.getOpenInventory().getTopInventory());
	}
	
}

























