package com.lollolcheese123.cut.train.engine;

import com.lollolcheese123.cut.train.TrainEngineData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public interface EngineHost {
	EngineTarget target();
	TrainEngineData data();
	void commit(TrainEngineData data);
	Level level();
	Vec3 worldCenter();
	boolean isValid();
	List<IItemHandler> itemSources();

	default BlockPos blockPos() {
		return BlockPos.containing(worldCenter());
	}
	default boolean inRange(Player p) {
		return p.distanceToSqr(worldCenter()) <= 64;
	}
}
