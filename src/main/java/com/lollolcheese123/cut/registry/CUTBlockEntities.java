package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CUTBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateUpgradableTrains.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrainEngineBlockEntity>> TRAIN_ENGINE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
			"train_engine", () -> BlockEntityType.Builder.of(
					TrainEngineBlockEntity::new,
					CUTBlocks.TRAIN_ENGINE.get()
			).build(null)
	);

	public static void register(IEventBus eventBus) {
		BLOCK_ENTITIES.register(eventBus);
	}
}
