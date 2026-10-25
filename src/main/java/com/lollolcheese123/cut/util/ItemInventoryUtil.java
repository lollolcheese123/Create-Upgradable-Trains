package com.lollolcheese123.cut.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ItemInventoryUtil {
	private ItemInventoryUtil() {}

	private static List<IItemHandler> neighbours(Level level, BlockPos pos) {
		List<IItemHandler> out = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			IItemHandler handler = level.getCapability(
					Capabilities.ItemHandler.BLOCK,
					pos.relative(dir),
					dir.getOpposite()
			);
			if (handler != null) out.add(handler);
		}
		return out;
	}

	private static List<IItemHandler> sources(Level level, BlockPos pos, Player player) {
		List<IItemHandler> out = neighbours(level, pos);
		out.add(new PlayerMainInvWrapper(player.getInventory()));
		return out;
	}

	public static void giveBack(Map<Item, Integer> refund, Level level, BlockPos pos, Player player) {
		IItemHandler playerInv = new PlayerMainInvWrapper(player.getInventory());
		List<IItemHandler> nearby = neighbours(level, pos);

		for (Map.Entry<Item, Integer> entry : refund.entrySet()) {
			Item item = entry.getKey();
			int left = entry.getValue();

			while (left > 0) {
				int count = Math.min(left, item.getDefaultMaxStackSize());
				left -= count;
				ItemStack rest = new ItemStack(item, count);
				rest = ItemHandlerHelper.insertItemStacked(playerInv, rest, false);

				for (IItemHandler handler : nearby) {
					if (rest.isEmpty()) break;
					rest = ItemHandlerHelper.insertItemStacked(handler, rest, false);
				}

				if (!rest.isEmpty()) player.drop(rest, false);
			}
		}
	}
}
