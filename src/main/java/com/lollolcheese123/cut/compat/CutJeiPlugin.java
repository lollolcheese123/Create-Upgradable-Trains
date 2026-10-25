package com.lollolcheese123.cut.compat;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@JeiPlugin
public class CutJeiPlugin implements IModPlugin {
	static IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "jei");
	}
	@Override
	public void onRuntimeAvailable(IJeiRuntime r) {
		runtime = r;
	}
	@Override
	public void onRuntimeUnavailable() {
		runtime = null;
	}
}

final class JeiCompat {
	static boolean show(ItemStack itemStack, boolean showUses) {
		IJeiRuntime runtime = CutJeiPlugin.runtime;
		if (runtime == null) return false;

		var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(
				showUses ? RecipeIngredientRole.INPUT: RecipeIngredientRole.OUTPUT,
				VanillaTypes.ITEM_STACK,
				itemStack
		);
		runtime.getRecipesGui().show(focus);
		return true;
	}
}
