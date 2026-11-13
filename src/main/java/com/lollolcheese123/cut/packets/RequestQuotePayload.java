package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.train.engine.EngineHost;
import com.lollolcheese123.cut.train.engine.EngineHosts;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import com.lollolcheese123.cut.train.engine.TrainEngineLogic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RequestQuotePayload(
		EngineTarget target,
		ResourceLocation id
) implements CustomPacketPayload {

	public static final Type<RequestQuotePayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "quote_request"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestQuotePayload> STREAM_CODEC =
			StreamCodec.composite(
					EngineTarget.STREAM_CODEC,
					RequestQuotePayload::target,
					ResourceLocation.STREAM_CODEC,
					RequestQuotePayload::id,
					RequestQuotePayload::new
			);

	@Override public  @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RequestQuotePayload payload, IPayloadContext ctx) {
		ServerPlayer player = (ServerPlayer) ctx.player();
		EngineHost host = EngineHosts.resolve(player.level(), payload.target());
		if (host == null || !host.isValid() || !host.inRange(player)) return;

		PacketDistributor.sendToPlayer(
				player,
				new QuotePayload(
						payload.target(),
						payload.id(),
						TrainEngineLogic.quote(host, payload.id(), player)
				)
		);
	}
}
