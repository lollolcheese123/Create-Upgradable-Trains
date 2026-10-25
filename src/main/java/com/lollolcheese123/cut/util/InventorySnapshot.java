package com.lollolcheese123.cut.util;

import com.lollolcheese123.cut.compat.NumismaticsCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.*;
import java.util.function.Predicate;

public final class InventorySnapshot {
	private static final class Slot {
		final IItemHandler handler;
		final int index;
		final ItemStack stack;
		int free;
		int used;

		Slot(IItemHandler handler, int index, ItemStack itemStack) {
			this.handler = handler;
			this.index = index;
			this.stack = itemStack.copy();
			this.free = this.stack.getCount();
		}
	}
	private final List<Slot> slots = new ArrayList<>();

	public static InventorySnapshot of(Level level, BlockPos pos, Player player) {
		InventorySnapshot snapshot = new InventorySnapshot();

		for (Direction dir : Direction.values()) {
			IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
			if (handler != null) snapshot.add(handler);
		}

		snapshot.add(new PlayerMainInvWrapper(player.getInventory()));

		return snapshot;
	}

	private void add(IItemHandler handler) {
		for (int index = 0; index < handler.getSlots(); index++) {
			ItemStack stack = handler.getStackInSlot(index);
			if (!stack.isEmpty()) slots.add(new Slot(handler, index, stack));
		}
	}

	public int count(Predicate<ItemStack> predicate) {
		int amount = 0;
		for (Slot slot : slots) if (predicate.test(slot.stack)) amount += slot.free;
		return amount;
	}

	public Map<Item, Integer> take(Predicate<ItemStack> predicate, int amount) {
		if (count(predicate) < amount) return null;
		Map<Item, Integer> taken = new HashMap<>();

		for (Slot slot : slots) {
			if (amount <= 0) break;
			if (slot.free == 0 || !predicate.test(slot.stack)) continue;

			int count = Math.min(amount, slot.free);
			slot.free -= count;
			slot.used += count;
			amount -= count;

			taken.merge(slot.stack.getItem(), count, Integer::sum);
		}

		return taken;
	}

	public int moneyTotal() {
		if (!ModList.get().isLoaded("numismatics")) return 0;

		int sum = 0;
		for (Slot slot : slots) sum += NumismaticsCompat.getDenominationValue(slot.stack) * slot.free;
		return sum;
	}

	public int takeMoney(int amount) {
		if (!ModList.get().isLoaded("numismatics")) return amount <= 0 ? 0 : -1;

		if (moneyTotal() < amount) return -1;
		List<Slot> coins = new ArrayList<>();
		for (Slot slot : slots)
			if (slot.free > 0 && NumismaticsCompat.getDenominationValue(slot.stack) > 0)
				coins.add(slot);
		coins.sort(Comparator.comparingInt((Slot slot) -> NumismaticsCompat.getDenominationValue(slot.stack)));

		int paid = 0;
		for (Slot slot : coins) {
			if (paid >= amount) break;

			int value = NumismaticsCompat.getDenominationValue(slot.stack);
			int count = Math.min(slot.free, (amount - paid + value - 1) / value);
			slot.free -= count;
			slot.used += count;
			paid += value * count;
		}

		return paid - amount;
	}

	public void commit() {
		for (Slot slot : slots) if (slot.used > 0) slot.handler.extractItem(slot.index, slot.used, false);
	}
}
