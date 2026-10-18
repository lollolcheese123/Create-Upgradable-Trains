package com.lollolcheese123.cut.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lollolcheese123.cut.duck.CarriageAddons;
import com.lollolcheese123.cut.train.TrainTypes;
import com.lollolcheese123.cut.util.CutCodecs;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Carriage.class)
public class CarriageMixin implements CarriageAddons {
	@Unique private ResourceLocation CUT$trainTypeId = TrainTypes.DEFAULT_ID;

	@Override public ResourceLocation CUT$getTrainTypeId() {
		return this.CUT$trainTypeId;
	}
	@Override public void CUT$setTrainTypeId(ResourceLocation resourceLocation) {
		this.CUT$trainTypeId = resourceLocation;
	}

	@Shadow @Final @Mutable
	public static StreamCodec<RegistryFriendlyByteBuf, Carriage> STREAM_CODEC;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void CUT$wrapCodec(CallbackInfo ci) {
		STREAM_CODEC = CutCodecs.carriage(STREAM_CODEC);
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
			TrackGraph graph,
			DimensionPalette dimensions,
			CallbackInfoReturnable<Carriage> cir
	) {
		if (tag.contains("CUTTrainType"))
			((CarriageAddons) cir.getReturnValue())
					.CUT$setTrainTypeId(ResourceLocation.parse(tag.getString("CUTTrainType")));
	}
}
