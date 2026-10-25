package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateUpgradableTrains.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_UPGRADABLE_TRAINS = TABS.register(
			"create_upgradable_trains",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("cut.item_group.name"))
					.icon(() -> CUTItems.TRAIN_ENGINE_ITEM.get().getDefaultInstance())
					.displayItems((parameters, output) -> {
						output.accept(CUTItems.TRAIN_ENGINE_ITEM.get());
					})
					.build());

	public static void register(IEventBus eventBus) {
		TABS.register(eventBus);
	}
}