package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lollolcheese123.cut.blocks.TrainEngineBlock;
import com.lollolcheese123.cut.duck.CarriageAddons;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(StationBlockEntity.class)
public class StationBlockEntityMixin {

	@Inject(method = "assemble", at = @At(
			value = "INVOKE",
			target = "Lcom/simibubi/create/content/trains/entity/Train;collectInitiallyOccupiedSignalBlocks()V",
			shift = At.Shift.BEFORE
	))
	private void getTrainType(
			UUID playerUUID,
			CallbackInfo ci,
			@Local(name = "contraptions") List<CarriageContraption> contraptions,
			@Local(name = "train") Train train
	) {
		TrainType bestType = TrainTypes.getDefault(false);
		ResourceLocation bestTypeId = TrainTypes.DEFAULT_ID;
		for (int i = 0; i < contraptions.size(); i++) {
			CarriageContraption contraption = contraptions.get(i);
			CarriageAddons carriage = (CarriageAddons) train.carriages.get(i);

			Optional<ResourceLocation> resourceLocation = CUT$getEngineBlockTrainTypeId(contraption);
			if (resourceLocation.isEmpty()) {
				carriage.CUT$setTrainTypeId(TrainTypes.DEFAULT_ID);
				continue;
			}

			carriage.CUT$setTrainTypeId(resourceLocation.get());
			TrainType curType = TrainTypes.lookupOrFallback(false, resourceLocation.get());

			if (TrainType.RANKING.compare(bestType, curType) < 0) {
				bestType = curType;
				bestTypeId = resourceLocation.get();
			}
		}
		((TrainAddons) train).CUT$setTrainTypeId(bestTypeId);
	}

	@Unique
	private static Optional<ResourceLocation> CUT$getEngineBlockTrainTypeId(CarriageContraption contraption) {
		for (StructureTemplate.StructureBlockInfo structureBlockInfo : contraption.getBlocks().values()) {
			if (!(structureBlockInfo.state().getBlock() instanceof TrainEngineBlock)) continue;

			CompoundTag nbt = structureBlockInfo.nbt();

			if (nbt == null || !nbt.contains("CUTTrainType")) continue;

			return Optional.ofNullable(ResourceLocation.tryParse(nbt.getString("CUTTrainType")));
		}
		return Optional.empty();
	}
}
