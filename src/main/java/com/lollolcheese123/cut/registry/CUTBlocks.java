package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.TrainEngineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateUpgradableTrains.MODID);

	public static final DeferredBlock<TrainEngineBlock> TRAIN_ENGINE = BLOCKS.registerBlock(
			"train_engine",
			TrainEngineBlock::new,
			BlockBehaviour.Properties.of()
	);

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}
