package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lollolcheese123.cut.train.TrainTypeResolver;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(StationBlockEntity.class)
public abstract class StationBlockEntityMixin {

	@Inject(method = "assemble", at = @At(
			value = "INVOKE",
			target = "Lcom/simibubi/create/content/trains/entity/Train;collectInitiallyOccupiedSignalBlocks()V",
			shift = At.Shift.BEFORE
	))
	private void CUT$assignTrainType(
			UUID playerUUID,
			CallbackInfo ci,
			@Local(name = "contraptions") List<CarriageContraption> contraptions,
			@Local(name = "train") Train train
	) {
		TrainTypeResolver.assign(train, contraptions);
	}
}
