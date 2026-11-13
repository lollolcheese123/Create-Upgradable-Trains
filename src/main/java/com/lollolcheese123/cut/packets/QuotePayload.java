package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.train_engine.TrainEngineBlockEntity;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import com.lollolcheese123.cut.train.engine.TrainEngineLogic;
import com.lollolcheese123.cut.util.ItemOrTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record QuotePayload(
		EngineTarget target,
		ResourceLocation id,
		TrainEngineLogic.Quote quote
) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<Item, Integer>> ITEM_MAP =
			ByteBufCodecs.map(
					HashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT
			);

	private static final StreamCodec<RegistryFriendlyByteBuf, TrainEngineLogic.Line> LINE =
			StreamCodec.composite(
					ItemOrTag.STREAM_CODEC,
					TrainEngineLogic.Line::target,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Line::total,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Line::reused,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Line::have,
					TrainEngineLogic.Line::new
			);

	private static final StreamCodec<RegistryFriendlyByteBuf, TrainEngineLogic.Quote> QUOTE =
			StreamCodec.composite(
					ByteBufCodecs.BOOL,
					TrainEngineLogic.Quote::affordable,
					LINE.apply(ByteBufCodecs.list()),
					TrainEngineLogic.Quote::lines,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Quote::moneyNeeded,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Quote::moneyHave,
					ITEM_MAP,
					TrainEngineLogic.Quote::refund,
					ByteBufCodecs.VAR_INT,
					TrainEngineLogic.Quote::refundMoney,
					TrainEngineLogic.Quote::new
			);

	public static final Type<QuotePayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "quote_response"));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuotePayload> STREAM_CODEC =
			StreamCodec.composite(
					EngineTarget.STREAM_CODEC,
					QuotePayload::target,
					ResourceLocation.STREAM_CODEC,
					QuotePayload::id,
					QUOTE,
					QuotePayload::quote,
					QuotePayload::new
			);

	@Override public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
