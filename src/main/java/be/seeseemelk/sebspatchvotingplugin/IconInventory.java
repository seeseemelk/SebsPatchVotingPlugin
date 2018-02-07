package be.seeseemelk.sebspatchvotingplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import static org.bukkit.Material.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class IconInventory
{
	private final Inventory inventory;
	private final Material[] materials = {
			GRASS, DIRT,      STONE,              COBBLESTONE, WOOD,          APPLE,    LOG,               LEAVES, GLASS,
			SAND,  PUMPKIN,   ICE,                GOLD_ORE,    IRON_ORE,      COAL_ORE, YELLOW_FLOWER,     FLINT,  BRICK,
			TNT,   BOOKSHELF, MOSSY_COBBLESTONE,  CHEST,       DIAMOND_BLOCK, SIGN,     REDSTONE_TORCH_ON, TORCH,  BARRIER
			
	};

	public IconInventory()
	{
		inventory = Bukkit.createInventory(null, 9*3, "Select an icon");
		for (Material material : materials)
			inventory.addItem(new ItemStack(material));
	}
	
	/**
	 * Gets the inventory to be used by bukkit.
	 * 
	 * @return A Inventory object that can be used by bukkit.
	 */
	public Inventory getInventory()
	{
		return inventory;
	}

}
