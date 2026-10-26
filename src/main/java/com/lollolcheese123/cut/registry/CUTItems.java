package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.blockitem.TrainEngineBlockItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateUpgradableTrains.MODID);

	public static final DeferredItem<BlockItem> TRAIN_ENGINE_ITEM = ITEMS.registerItem(
			"train_engine",
			props -> new TrainEngineBlockItem(CUTBlocks.TRAIN_ENGINE.get(), props)
	);

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
