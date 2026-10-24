package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.train.TrainType;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SyncTrainTypesPayload(
		Map<ResourceLocation, TrainType> types
) implements CustomPacketPayload {

	public static final Type<SyncTrainTypesPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "sync_train_types"));

	public static final StreamCodec<ByteBuf, SyncTrainTypesPayload> STREAM_CODEC =
			ByteBufCodecs.fromCodec(Codec.unboundedMap(ResourceLocation.CODEC, TrainType.SEND_CODEC))
					.map(SyncTrainTypesPayload::new, SyncTrainTypesPayload::types);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
