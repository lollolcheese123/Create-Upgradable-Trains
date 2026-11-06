package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.CutCodecs;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
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

	@Unique @Override
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

	@Unique @Override
	public float CUT$deceleration(float accel) {
		return accel * CUT$getTrainType().decelModToApplyToAccel();
	}

//	@Redirect(method = "tickPassiveSlowdown",
//				at = @At(value = "INVOKE",
//						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
//	private float CUT$passiveDecel(Train train) {
	@WrapOperation(method = "tickPassiveSlowdown",
					at = @At(value = "INVOKE",
							target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$passiveDecel(Train train, Operation<Float> original) {
		return ((TrainAddons) train).CUT$deceleration(original.call(train));
	}

//	@Redirect(method = "approachTargetSpeed",
//				at = @At(value = "INVOKE",
//						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
//	private float CUT$targetSpeedApproachAccelRate(
//			Train train,
//			@Local(name = "actualTarget") double actualTarget
//	) {
	@WrapOperation(method = "approachTargetSpeed",
		at = @At(value = "INVOKE",
				target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$targetSpeedApproachAccelRate(
			Train train, Operation<Float> original,
			@Local(name = "actualTarget") double actualTarget
	) {
		float accel = original.call(train);
		boolean braking = speed != 0 && (Math.abs(actualTarget) < Math.abs(speed)
				|| Math.signum(speed) != Math.signum(actualTarget));
		return braking ? CUT$deceleration(accel) : accel;
	}

//	@Inject(method = "burnFuel", at = @At("HEAD"), cancellable = true)
//	private void CUT$burnFuel(CallbackInfo ci) {
	@WrapMethod(method = "burnFuel")
	private void CUT$burnFuel(Operation<Void> original) {
		int before = fuelTicks;
		original.call();
		int consumed = before - fuelTicks;
		if (consumed <= 0) return;
		if (fuelTicks <= 0) return;

		CUT$fuelRemainder += consumed * CUT$getTrainType().trueFuelMult();
		int whole = (int) CUT$fuelRemainder;
		CUT$fuelRemainder -= whole;

		fuelTicks = Math.max(0, before - whole);
	}
}
