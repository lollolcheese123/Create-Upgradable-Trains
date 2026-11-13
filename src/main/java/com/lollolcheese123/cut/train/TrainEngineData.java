package com.lollolcheese123.cut.train;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class TrainEngineData {
	private static final Codec<Map<Item, Integer>> PAID_CODEC =
			Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT);

	public ResourceLocation trainTypeId = TrainTypes.DEFAULT_ID;
	public Map<Item, Integer> paidItems = new HashMap<>();
	public int paidMoney;

	public void save(CompoundTag tag) {
		tag.putString("CUTTrainType", trainTypeId.toString());
		PAID_CODEC.encodeStart(NbtOps.INSTANCE, paidItems).result().ifPresent(t -> tag.put("paid_items", t));
		tag.putInt("paid_money", paidMoney);
	}
	public static TrainEngineData load(CompoundTag tag) {
		TrainEngineData data = new TrainEngineData();

		ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("CUTTrainType"));
		if (parsed != null) data.trainTypeId = parsed;
		if (tag.contains("paid_items"))
			PAID_CODEC.parse(
					NbtOps.INSTANCE,
					tag.get("paid_items")).result().ifPresent(map -> {
				data.paidItems = new HashMap<>(map);
				data.paidItems.values().removeIf(value -> value <= 0);
			});
		data.paidMoney = tag.getInt("paid_money");

		return data;
	}
	public TrainEngineData copy() {
		TrainEngineData data = new TrainEngineData();

		data.trainTypeId = this.trainTypeId;
		data.paidItems = new HashMap<>(this.paidItems);
		data.paidMoney = this.paidMoney;

		return data;
	}
	public boolean isDefault() {
		return trainTypeId.equals(TrainTypes.DEFAULT_ID);
	}
}
