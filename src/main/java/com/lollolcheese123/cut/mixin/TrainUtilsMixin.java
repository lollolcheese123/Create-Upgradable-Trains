package com.lollolcheese123.cut.mixin;

import com.lollolcheese123.cut.duck.CarriageAddons;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.railwayteam.railways.content.coupling.TrainUtils")
public abstract class TrainUtilsMixin {

	@Inject(method = "splitTrain", at = @At("RETURN"))
	private static void CUT$afterSplit(
			Train train,
			int numberOffEnd,
			CallbackInfoReturnable<Train> cir
	) {
		Train newTrain = cir.getReturnValue();
		if (newTrain == train) return;


		TrainAddons oldTrainData = (TrainAddons) train;
		TrainAddons newTrainData = (TrainAddons) newTrain;

		oldTrainData.CUT$setTrainTypeId(CUT$getMaxTrain(train));
		newTrainData.CUT$setTrainTypeId(CUT$getMaxTrain(newTrain));
	}

	@Unique
	private static ResourceLocation CUT$getMaxTrain(Train train) {
		TrainType best = null;
		for (Carriage carriage : train.carriages) {
			TrainType type = TrainTypes.lookupOrFallback(
					false,
					((CarriageAddons) carriage).CUT$getTrainTypeId()
			);
			if (best == null || TrainType.RANKING.compare(type, best) > 0) best = type;
		}
		return best != null ? best.id() : TrainTypes.DEFAULT_ID;
	}

	@Inject(method = "combineTrains(" +
			"Lcom/simibubi/create/content/trains/entity/Train;" +
			"Lcom/simibubi/create/content/trains/entity/Train;" +
			"Lnet/minecraft/world/phys/Vec3;" +
			"Lnet/minecraft/world/level/Level;I)" +
			"Lcom/simibubi/create/content/trains/entity/Train;",
			at = @At("RETURN"))
	private static void CUT$afterCombine(
			Train frontTrain,
			Train backTrain,
			Vec3 pos,
			Level level,
			int spacing,
			CallbackInfoReturnable<Train> cir
	) {
		if (!backTrain.carriages.isEmpty()) return;

		TrainAddons frontTrainData = (TrainAddons) frontTrain;
		TrainAddons backTrainData = (TrainAddons) backTrain;

		TrainType frontType = TrainTypes.lookupOrFallback(false, frontTrainData.CUT$getTrainTypeId());
		TrainType backType = TrainTypes.lookupOrFallback(false, backTrainData.CUT$getTrainTypeId());

		if (TrainType.RANKING.compare(backType, frontType) > 0)
			frontTrainData.CUT$setTrainTypeId(backTrainData.CUT$getTrainTypeId());
	}
}
