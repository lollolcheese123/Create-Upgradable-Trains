package com.lollolcheese123.cut.train.engine;

import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlockEntity;
import com.lollolcheese123.cut.train.TrainEngineData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public record BlockEntityHost(TrainEngineBlockEntity be) implements EngineHost {
	public EngineTarget target() {
		return new EngineTarget.InWorld(be.getBlockPos());
	}
	public TrainEngineData data() {
		return be.getData().copy();
	}
	public void commit(TrainEngineData d) {
		be.setData(d);
	}
	public Level level() {
		return be.getLevel();
	}
	public Vec3 worldCenter() {
		return Vec3.atCenterOf(be.getBlockPos());
	}
	public boolean isValid() {
		return !be.isRemoved() && be.getLevel() != null;
	}
	@Override
	public List<IItemHandler> itemSources() {
		List<IItemHandler> out = new ArrayList<>();
		Level level = be.getLevel();
		BlockPos pos = be.getBlockPos();
		if (level == null) return out;
		for (Direction dir : Direction.values()) {
			IItemHandler handler = level.getCapability(
					Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
			if (handler != null) out.add(handler);
		}
		return out;
	}
	@Override public BlockPos blockPos() {
		return be.getBlockPos();
	}
}
