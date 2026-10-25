package com.lollolcheese123.cut.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

public final class RecipeViewerCompat {
	private RecipeViewerCompat() {}

	@Nullable
	private static Screen returnScreen;

	public static boolean show(ItemStack itemStack, @Nullable TagKey<Item> tag, boolean showUses) {
		Screen current = Minecraft.getInstance().screen;
		if (current != null && !isEmiScreen(current)) returnScreen = current;

		if (ModList.get().isLoaded("emi")) return EmiCompat.show(itemStack, tag, showUses);
		if (ModList.get().isLoaded("jei")) return JeiCompat.show(itemStack, showUses);
		return false;
	}

	public static boolean isEmiScreen(@Nullable Screen s) {
		return s != null && s.getClass().getName().startsWith("dev.emi.emi.");
	}

	@Nullable
	public static Screen takeReturnScreen() {
		Screen s = returnScreen;
		returnScreen = null;
		return s;
	}
}
