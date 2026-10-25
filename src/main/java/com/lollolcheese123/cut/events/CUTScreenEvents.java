package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.compat.RecipeViewerCompat;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = CreateUpgradableTrains.MODID, value = Dist.CLIENT)
public final class CUTScreenEvents {

	@SubscribeEvent
	public static void onOpening(ScreenEvent.Opening event) {
		Screen from = event.getCurrentScreen();
		Screen to = event.getNewScreen();

		if (!RecipeViewerCompat.isEmiScreen(from) || RecipeViewerCompat.isEmiScreen(to)) return;

		Screen back = RecipeViewerCompat.takeReturnScreen();
		if (back != null) event.setNewScreen(back);
	}
}
