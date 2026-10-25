package com.lollolcheese123.cut.packets;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
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
		BlockPos pos,
		ResourceLocation id,
		TrainEngineBlockEntity.Quote quote
) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<Item, Integer>> ITEM_MAP =
			ByteBufCodecs.map(
					HashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT
			);

	private static final StreamCodec<RegistryFriendlyByteBuf, TrainEngineBlockEntity.Line> LINE =
			StreamCodec.composite(
					ItemOrTag.STREAM_CODEC,
					TrainEngineBlockEntity.Line::target,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Line::total,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Line::reused,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Line::have,
					TrainEngineBlockEntity.Line::new
			);

	private static final StreamCodec<RegistryFriendlyByteBuf, TrainEngineBlockEntity.Quote> QUOTE =
			StreamCodec.composite(
					ByteBufCodecs.BOOL,
					TrainEngineBlockEntity.Quote::affordable,
					LINE.apply(ByteBufCodecs.list()),
					TrainEngineBlockEntity.Quote::lines,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Quote::moneyNeeded,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Quote::moneyHave,
					ITEM_MAP,
					TrainEngineBlockEntity.Quote::refund,
					ByteBufCodecs.VAR_INT,
					TrainEngineBlockEntity.Quote::refundMoney,
					TrainEngineBlockEntity.Quote::new
			);

	public static final Type<QuotePayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "quote_response"));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuotePayload> STREAM_CODEC =
			StreamCodec.composite(
					BlockPos.STREAM_CODEC,
					QuotePayload::pos,
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
