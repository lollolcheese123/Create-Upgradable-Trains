package com.lollolcheese123.cut.train;

import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlock;
import com.lollolcheese123.cut.duck.CarriageAddons;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

// cut.train.TrainTypeResolver
public final class TrainTypeResolver {
	public static ResourceLocation engineTypeOf(Contraption contraption, boolean client) {
		TrainType best = TrainTypes.getDefault(client);
		ResourceLocation bestId = TrainTypes.DEFAULT_ID;

		for (StructureTemplate.StructureBlockInfo info : contraption.getBlocks().values()) {
			if (!(info.state().getBlock() instanceof TrainEngineBlock)) continue;
			CompoundTag nbt = info.nbt();
			if (nbt == null || !nbt.contains("CUTTrainType")) continue;

			ResourceLocation id = ResourceLocation.tryParse(nbt.getString("CUTTrainType"));
			if (id == null) continue;

			TrainType type = TrainTypes.lookupOrFallback(client, id);
			if (TrainType.RANKING.compare(best, type) < 0) {
				best = type;
				bestId = id;
			}
		}
		return bestId;
	}

	public static void recomputeTrain(Train train, boolean client) {
		TrainType best = TrainTypes.getDefault(client);
		ResourceLocation bestId = TrainTypes.DEFAULT_ID;
		for (Carriage c : train.carriages) {
			ResourceLocation id = ((CarriageAddons) c).CUT$getTrainTypeId();
			TrainType t = TrainTypes.lookupOrFallback(client, id);
			if (TrainType.RANKING.compare(best, t) < 0) {
				best = t;
				bestId = id;
			}
		}
		((TrainAddons) train).CUT$setTrainTypeId(bestId);
	}

	public static void refresh(CarriageContraptionEntity entity, boolean client) {
		Carriage carriage = entity.getCarriage();
		if (carriage == null) return;
		((CarriageAddons) carriage).CUT$setTrainTypeId(engineTypeOf(entity.getContraption(), client));
		recomputeTrain(carriage.train, client);
	}

	public static void assign(Train train, List<CarriageContraption> contraptions) {
		for (int i = 0; i < contraptions.size(); i++) {
			((CarriageAddons) train.carriages.get(i))
					.CUT$setTrainTypeId(engineTypeOf(contraptions.get(i), false));
		}
		recomputeTrain(train, false);
	}
}
