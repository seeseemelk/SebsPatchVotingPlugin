package be.seeseemelk.sebspatchvotingplugin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class SebsPatchVotingPluginTest
{
	@SuppressWarnings("unused")
	private ServerMock server;
	@SuppressWarnings("unused")
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
	public void showVoteInventory_OpensInventory()
	{
		PlayerMock player = server.addPlayer();
		plugin.showVoteInventory(player);
		assertSame(plugin.getVoteInventory(), player.getOpenInventory().getTopInventory());
	}

}
