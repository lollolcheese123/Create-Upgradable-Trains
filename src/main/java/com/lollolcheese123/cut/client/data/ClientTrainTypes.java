package com.lollolcheese123.cut.client.data;

import com.lollolcheese123.cut.train.TrainType;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public class ClientTrainTypes {
	private static volatile Map<ResourceLocation, TrainType> types = Map.of();

	public static void set(Map<ResourceLocation, TrainType> newTypes) {
		types = Map.copyOf(newTypes);
	}

	public static @Nullable TrainType get(ResourceLocation id) { return types.get(id); }
}
