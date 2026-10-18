package com.lollolcheese123.cut.blocks.blockentity;

import com.lollolcheese123.cut.registry.CUTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrainEngineBlockEntity extends BlockEntity {

	public TrainEngineBlockEntity(BlockPos pos, BlockState bs) {
		super(CUTBlockEntities.TRAIN_ENGINE_BLOCK_ENTITY.get(), pos, bs);
	}
}
