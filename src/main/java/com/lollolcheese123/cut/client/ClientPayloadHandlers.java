package com.lollolcheese123.cut.client;

import com.lollolcheese123.cut.packets.EngineDataSyncPayload;
import com.lollolcheese123.cut.train.TrainTypeResolver;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandlers {
	public static void handleEngineSync(EngineDataSyncPayload p, IPayloadContext ctx) {
		Level level = Minecraft.getInstance().level;
		if (level == null) return;
		if (!(level.getEntity(p.entityId()) instanceof AbstractContraptionEntity e)) return;

		var blocks = e.getContraption().getBlocks();
		StructureTemplate.StructureBlockInfo old = blocks.get(p.localPos());
		if (old == null) return;
		blocks.put(p.localPos(), new StructureTemplate.StructureBlockInfo(old.pos(), old.state(), p.nbt()));
		if (e instanceof CarriageContraptionEntity cce) TrainTypeResolver.refresh(cce, true);
	}
}
