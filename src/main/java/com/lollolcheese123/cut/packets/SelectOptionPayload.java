package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlockEntity;
import com.lollolcheese123.cut.train.engine.EngineHost;
import com.lollolcheese123.cut.train.engine.EngineHosts;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import com.lollolcheese123.cut.train.engine.TrainEngineLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SelectOptionPayload(
		EngineTarget target,
		ResourceLocation id
) implements CustomPacketPayload {

	public static final Type<SelectOptionPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "select_option"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SelectOptionPayload> STREAM_CODEC =
			StreamCodec.composite(
					EngineTarget.STREAM_CODEC,
					SelectOptionPayload::target,
					ResourceLocation.STREAM_CODEC,
					SelectOptionPayload::id,
					SelectOptionPayload::new
			);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SelectOptionPayload payload, IPayloadContext ctx) {
		ServerPlayer player = (ServerPlayer) ctx.player();
		EngineHost host = EngineHosts.resolve(player.level(), payload.target());
		if (host == null || !host.isValid() || !host.inRange(player)) return;

		TrainEngineLogic.select(host, payload.id(), player);
	}
}
