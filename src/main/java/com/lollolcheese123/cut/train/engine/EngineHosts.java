package com.lollolcheese123.cut.train.engine;

import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlockEntity;
import com.lollolcheese123.cut.contraption.ContraptionHost;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EngineHosts {
	public static @Nullable EngineHost resolve(Level level, EngineTarget target) {
		return switch (target) {
			case EngineTarget.InWorld w ->
				level.getBlockEntity(w.pos()) instanceof TrainEngineBlockEntity be ? new BlockEntityHost(be) : null;
			case EngineTarget.OnContraption c ->
				level.getEntity(c.entityId()) instanceof AbstractContraptionEntity e ? new ContraptionHost(e, c.localPos()) : null;
		};
	}
}
