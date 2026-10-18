package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.CutCodecs;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(Train.class)
public abstract class TrainMixin implements TrainAddons {
	@Unique private ResourceLocation CUT$trainTypeId = TrainTypes.DEFAULT_ID;

	@Override public ResourceLocation CUT$getTrainTypeId() {
		return this.CUT$trainTypeId;
	}
	@Override public void CUT$setTrainTypeId(ResourceLocation resourceLocation) {
		this.CUT$trainTypeId = resourceLocation;
	}

	@Shadow @Final @Mutable
	public static StreamCodec<RegistryFriendlyByteBuf, Train> STREAM_CODEC;
	@Shadow public int fuelTicks;
	@Shadow public double speed;

	@Shadow
	public abstract float acceleration();

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void CUT$wrapCodec(CallbackInfo ci) {
		STREAM_CODEC = CutCodecs.train(STREAM_CODEC);
	}

	@Inject(method = "write", at = @At("RETURN"))
	private void CUT$write(
			DimensionPalette dimensions,
			HolderLookup.Provider registries,
			CallbackInfoReturnable<CompoundTag> cir
	) {
		cir.getReturnValue().putString("CUTTrainType", CUT$trainTypeId.toString());
	}
	@Inject(method = "read", at = @At("RETURN"))
	private static void CUT$read(
			CompoundTag tag,
			HolderLookup.Provider registries,
			Map<UUID, TrackGraph> trackNetworks,
			DimensionPalette dimensions,
			CallbackInfoReturnable<Train> cir
	) {
		if (tag.contains("CUTTrainType"))
			((TrainAddons) cir.getReturnValue())
					.CUT$setTrainTypeId(ResourceLocation.parse(tag.getString("CUTTrainType")));
	}

	@ModifyReturnValue(method = "maxSpeed", at = @At("RETURN"))
	private float alterMaxSpeed(float original) {
		return original * 1000;
	}

	@ModifyReturnValue(method = "maxTurnSpeed", at = @At("RETURN"))
	private float alterMaxTurnSpeed(float original) {
		return original * 1000;
	}

	@ModifyReturnValue(method = "acceleration", at = @At("RETURN"))
	private float alterAcceleration(float original) {
		return original * 10;
	}

	@Redirect(method = "tickPassiveSlowdown",
				at = @At(value = "INVOKE",
						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$passiveDecel(Train train) {
		return ((TrainAddons) train).CUT$deceleration();
	}

	@Redirect(method = "approachTargetSpeed",
				at = @At(value = "INVOKE",
						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$targetSpeedApproachAccelRate(
			Train train,
			@Local(name = "actualTarget") double actualTarget
	) {
		boolean braking = speed != 0 && (Math.abs(actualTarget) < Math.abs(speed)
				|| Math.signum(speed) != Math.signum(actualTarget));
		return braking ? CUT$deceleration() : acceleration();
	}

	@Unique
	public float CUT$deceleration() {
		float originalDecel = (fuelTicks > 0 ? AllConfigs.server().trains.poweredTrainAcceleration.getF()
				: AllConfigs.server().trains.trainAcceleration.getF()) / 400;

		return originalDecel * 10;
	}
}
