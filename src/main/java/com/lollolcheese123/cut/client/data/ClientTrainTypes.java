package com.lollolcheese123.cut.client.data;

import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class ClientTrainTypes {
	private static volatile Map<ResourceLocation, TrainType> types = Map.of();

	public static void set(Map<ResourceLocation, TrainType> newTypes) {
		types = Map.copyOf(newTypes);
	}

	public static Collection<TrainType> getAll() {
		return types.values();
	}

	public static @Nullable TrainType get(ResourceLocation id) { return types.get(id); }
	public static TrainType getOrDefault(ResourceLocation id) {
		TrainType type = types.get(id);
		if (type != null) return type;
		TrainType defaultType = types.get(TrainTypes.DEFAULT_ID);
		if (defaultType != null) return defaultType;
		return TrainType.FALLBACK;
	}

	public static float getMaxSpeedMult() {
		float max = TrainType.FALLBACK.maxSpeedMod();
		for (TrainType type : types.values()) {
			if (type.maxSpeedMod() > max) max = type.maxSpeedMod();
		}
		return max;
	}
	public static float getMaxTurnSpeedMult() {
		float max = TrainType.FALLBACK.maxTurnSpeedMod();
		for (TrainType type : types.values()) {
			if (type.maxSpeedMod() > max) max = type.maxTurnSpeedMod();
		}
		return max;
	}
	public static float getMaxAccelMult() {
		float max = TrainType.FALLBACK.accelMod();
		for (TrainType type : types.values()) {
			if (type.maxSpeedMod() > max) max = type.accelMod();
		}
		return max;
	}
	public static float getMaxDecelMult() {
		float max = TrainType.FALLBACK.decelMod();
		for (TrainType type : types.values()) {
			if (type.maxSpeedMod() > max) max = type.decelMod();
		}
		return max;
	}
	public static float getMaxFuelEff() {
		float max = TrainType.FALLBACK.fuelEffMod();
		for (TrainType type : types.values()) {
			if (type.maxSpeedMod() > max) max = type.fuelEffMod();
		}
		return max;
	}
}
