package com.lollolcheese123.cut.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

final class EmiCompat {
	private EmiCompat() {}

	static boolean show(ItemStack itemStack, @Nullable TagKey<Item> tag, boolean showUses) {
		EmiIngredient ingredient = tag != null ? EmiIngredient.of(tag) : EmiStack.of(itemStack);
		if (showUses) EmiApi.displayUses(ingredient);
		else EmiApi.displayRecipes(ingredient);
		return true;
	}
}
