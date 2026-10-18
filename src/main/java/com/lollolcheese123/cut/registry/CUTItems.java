package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateUpgradableTrains.MODID);

	public static final DeferredItem<BlockItem> TRAIN_ENGINE_ITEM = ITEMS.registerSimpleBlockItem(
			"train_engine",
			CUTBlocks.TRAIN_ENGINE
	);

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
