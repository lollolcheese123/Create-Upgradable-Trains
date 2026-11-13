package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record EngineDataSyncPayload(
		int entityId,
		BlockPos localPos,
		CompoundTag nbt
) implements CustomPacketPayload {

	public static final Type<EngineDataSyncPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "engine_data_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, EngineDataSyncPayload> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT, EngineDataSyncPayload::entityId,
					BlockPos.STREAM_CODEC, EngineDataSyncPayload::localPos,
					ByteBufCodecs.COMPOUND_TAG, EngineDataSyncPayload::nbt,
					EngineDataSyncPayload::new);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
