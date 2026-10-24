package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.CutCodecs;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(Train.class)
public abstract class TrainMixin implements TrainAddons {
	@Shadow public int fuelTicks;

	@Unique private ResourceLocation CUT$trainTypeId = TrainTypes.DEFAULT_ID;
	@Unique private float CUT$fuelRemainder;

	@Override public ResourceLocation CUT$getTrainTypeId() {
		return this.CUT$trainTypeId;
	}
	@Override public void CUT$setTrainTypeId(ResourceLocation resourceLocation) {
		this.CUT$trainTypeId = resourceLocation;
	}

	@Unique private Boolean clientSide = null;

	@Unique
	public boolean CUT$isClientSide() {
		if (clientSide != null) return clientSide;

		try {
			clientSide = this.carriages.getFirst().anyAvailableEntity().level().isClientSide;
		} catch (Exception e) {
			return false;
		}
		
		return clientSide;
	}

	@Shadow @Final @Mutable
	public static StreamCodec<RegistryFriendlyByteBuf, Train> STREAM_CODEC;
	@Shadow public double speed;

	@Shadow public abstract float acceleration();

	@Shadow
	public List<Carriage> carriages;

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

	@Unique
	private TrainType CUT$getTrainType() {
		return TrainTypes.lookupOrFallback(CUT$isClientSide(), CUT$trainTypeId);
	}

	@ModifyReturnValue(method = "maxSpeed", at = @At("RETURN"))
	private float CUT$alterMaxSpeed(float original) {
		return original * CUT$getTrainType().maxSpeedMod();
	}

	@ModifyReturnValue(method = "maxTurnSpeed", at = @At("RETURN"))
	private float CUT$alterMaxTurnSpeed(float original) {
		return original * CUT$getTrainType().maxTurnSpeedMod();
	}

	@ModifyReturnValue(method = "acceleration", at = @At("RETURN"))
	private float CUT$alterAcceleration(float original) {
		return original * CUT$getTrainType().accelMod();
	}

	@Unique
	public float CUT$deceleration() {
		float original = (fuelTicks > 0 ? AllConfigs.server().trains.poweredTrainAcceleration.getF()
				: AllConfigs.server().trains.trainAcceleration.getF()) / 400;

		return original * CUT$getTrainType().decelMod();
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

	@Inject(method = "burnFuel", at = @At("HEAD"), cancellable = true)
	private void CUT$burnFuel(CallbackInfo ci) {
		if (fuelTicks <= 0) return;

		CUT$fuelRemainder += CUT$getTrainType().trueFuelMult();
		int whole = (int) CUT$fuelRemainder;
		CUT$fuelRemainder -= whole;

		fuelTicks = Math.max(0, fuelTicks - whole);
		ci.cancel();
	}
}
