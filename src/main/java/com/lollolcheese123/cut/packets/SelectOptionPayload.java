package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SelectOptionPayload(
		BlockPos pos,
		ResourceLocation id
) implements CustomPacketPayload {

	public static final Type<SelectOptionPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "select_option"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SelectOptionPayload> CODEC =
			StreamCodec.composite(
					BlockPos.STREAM_CODEC, SelectOptionPayload::pos,
					ResourceLocation.STREAM_CODEC, SelectOptionPayload::id,
					SelectOptionPayload::new
			);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SelectOptionPayload p, IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		if (!level.isLoaded(p.pos()) || player.distanceToSqr(p.pos().getCenter()) > 64) return;
		if (level.getBlockEntity(p.pos()) instanceof TrainEngineBlockEntity be)
			be.select(p.id());
	}
}
