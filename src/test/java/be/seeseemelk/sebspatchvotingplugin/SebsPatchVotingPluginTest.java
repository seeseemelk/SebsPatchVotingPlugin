package be.seeseemelk.sebspatchvotingplugin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

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

}
