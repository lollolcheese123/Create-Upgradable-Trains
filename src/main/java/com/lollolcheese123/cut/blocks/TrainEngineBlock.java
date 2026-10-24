package com.lollolcheese123.cut.blocks;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.TrainEngineClient;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrainEngineBlock extends BaseEntityBlock implements EntityBlock, IWrenchable {
	public static final MapCodec<TrainEngineBlock> CODEC = simpleCodec(TrainEngineBlock::new);

	public TrainEngineBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		return new TrainEngineBlockEntity(blockPos, blockState);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected InteractionResult useWithoutItem(
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			BlockHitResult hit
	) {
		if (level.isClientSide) TrainEngineClient.openScreen(pos);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
