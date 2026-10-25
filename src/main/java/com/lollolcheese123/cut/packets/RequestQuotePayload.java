package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RequestQuotePayload(
		BlockPos pos,
		ResourceLocation id
) implements CustomPacketPayload {

	public static final Type<RequestQuotePayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "quote_request"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestQuotePayload> STREAM_CODEC =
			StreamCodec.composite(
					BlockPos.STREAM_CODEC,
					RequestQuotePayload::pos,
					ResourceLocation.STREAM_CODEC,
					RequestQuotePayload::id,
					RequestQuotePayload::new
			);

	@Override public  @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RequestQuotePayload payload, IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

		if (!level.isLoaded(payload.pos()) || player.distanceToSqr(payload.pos().getCenter()) > (reach + 2)*(reach + 2)) return;
		if (!(level.getBlockEntity(payload.pos()) instanceof TrainEngineBlockEntity be)) return;

		PacketDistributor.sendToPlayer((ServerPlayer) player, new QuotePayload(payload.pos(), payload.id(), be.quote(payload.id(), player)));
	}
}
