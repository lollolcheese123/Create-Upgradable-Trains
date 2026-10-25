package com.lollolcheese123.cut.compat;

import dev.ithundxr.createnumismatics.content.backend.Coin;
import dev.ithundxr.createnumismatics.registry.NumismaticsItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class NumismaticsCompat {
	public static int getStackValue(ItemStack stack) {
		return getDenominationValue(stack) * stack.getCount();
	}
	public static int getDenominationValue(ItemStack stack) {
		for (Coin coin : Coin.values()) {
			if (stack.is(NumismaticsItems.getCoin(coin).asItem()))
				return coin.value;
		}
		return 0;
	}

	public static Map<Item, Integer> toCoins(int amount) {
		Map<Item, Integer> out = new HashMap<>();
		List<Coin> values = new ArrayList<>(List.of(Coin.values()));
		values.sort(Comparator.comparingInt((Coin coin) -> -coin.value));

		for (Coin coin : values) {
			Item coinItem = NumismaticsItems.getCoin(coin).asItem();

			int count = amount / coin.value;
			if (amount > 0) out.put(coinItem, count);
			amount %= coin.value;
		}

		return out;
	}
}
