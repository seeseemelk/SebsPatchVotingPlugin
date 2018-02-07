package be.seeseemelk.sebspatchvotingplugin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class Utils
{
	/**
	 * Checks if an item is an empty item.
	 * An empty item is any item that is a {@code null},
	 * has the {@link Material} type {@link Material.AIR},
	 * or has a stack size of {@code 0}.
	 * @param item The item to check for.
	 * @return {@code true} if the item is considered empty, {@code false} if the item is not considered empty.
	 */
	public static boolean isEmptyItem(ItemStack item)
	{
		return item == null || item.getType() == Material.AIR || item.getAmount() == 0;
	}
}
