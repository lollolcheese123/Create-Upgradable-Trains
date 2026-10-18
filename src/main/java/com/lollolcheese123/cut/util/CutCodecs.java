package com.lollolcheese123.cut.util;

import com.lollolcheese123.cut.duck.CarriageAddons;
import com.lollolcheese123.cut.duck.TrainAddons;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class CutCodecs {
	private CutCodecs() {}

	public static StreamCodec<RegistryFriendlyByteBuf, Carriage> carriage(
			StreamCodec<RegistryFriendlyByteBuf, Carriage> original
	) {
		return StreamCodec.composite(
				original,
				c -> c,
				ResourceLocation.STREAM_CODEC,
				c -> ((CarriageAddons) c).CUT$getTrainTypeId(),
				(c, id) -> {
					((CarriageAddons) c).CUT$setTrainTypeId(id);
					return c;
				});
	}

	public static StreamCodec<RegistryFriendlyByteBuf, Train> train(
			StreamCodec<RegistryFriendlyByteBuf, Train> original
	) {
		return StreamCodec.composite(
				original,
				t -> t,
				ResourceLocation.STREAM_CODEC,
				t -> ((TrainAddons) t).CUT$getTrainTypeId(),
				(t, id) -> {
					((TrainAddons) t).CUT$setTrainTypeId(id);
					return t;
				});
	}
}
