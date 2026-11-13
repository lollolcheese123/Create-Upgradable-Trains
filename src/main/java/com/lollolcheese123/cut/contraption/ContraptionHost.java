package com.lollolcheese123.cut.contraption;

import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlock;
import com.lollolcheese123.cut.packets.EngineDataSyncPayload;
import com.lollolcheese123.cut.train.TrainEngineData;
import com.lollolcheese123.cut.train.TrainTypeResolver;
import com.lollolcheese123.cut.train.engine.EngineHost;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public record ContraptionHost(
		AbstractContraptionEntity entity,
		BlockPos localPos
) implements EngineHost {
	public EngineTarget target() {
		return new EngineTarget.OnContraption(entity.getId(), localPos);
	}
	public Level level() {
		return entity.level();
	}
	public Vec3 worldCenter() {
		return entity.toGlobalVector(Vec3.atCenterOf(localPos), 1f);
	}

	private StructureTemplate.StructureBlockInfo info() {
		return entity.getContraption().getBlocks().get(localPos);
	}

	public boolean isValid() {
		StructureTemplate.StructureBlockInfo info = info();
		return entity.isAlive() && info != null && info.state().getBlock() instanceof TrainEngineBlock;
	}
	@Override
	public List<IItemHandler> itemSources() {
		return List.of(entity.getContraption().getStorage().getMountedItems());
	}

	public TrainEngineData data() {
		CompoundTag nbt = info().nbt();
		return nbt == null ? new TrainEngineData() : TrainEngineData.load(nbt);
	}

	public void commit(TrainEngineData data) {
		StructureTemplate.StructureBlockInfo old = info();
		CompoundTag nbt = old.nbt() == null ? new CompoundTag() : old.nbt().copy();
		data.save(nbt);
		entity.getContraption().getBlocks().put(
				localPos,
				new StructureTemplate.StructureBlockInfo(old.pos(), old.state(), nbt)
		);
		PacketDistributor.sendToPlayersTrackingEntity(
				entity,
				new EngineDataSyncPayload(entity.getId(), localPos, nbt)
		);
		if (entity instanceof CarriageContraptionEntity cce)
			TrainTypeResolver.refresh(cce, false);
	}
}
