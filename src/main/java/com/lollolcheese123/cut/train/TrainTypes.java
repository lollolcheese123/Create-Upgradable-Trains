package com.lollolcheese123.cut.train;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class TrainTypes {
	public static final ResourceLocation DEFAULT_ID =
			ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "default");

	public static @Nullable TrainType lookup(boolean isClient, ResourceLocation id) {
		return isClient
				? ClientTrainTypes.get(id)
				: TrainTypeManager.INSTANCE.get(id);
	}

	public static TrainType lookupOrFallback(boolean isClient, ResourceLocation id) {
		TrainType type = lookup(isClient, id);
		if (type != null) return type;
		TrainType custom = lookup(isClient, DEFAULT_ID);
		return custom != null ? custom : TrainType.FALLBACK;
	}

	public static TrainType getDefault(boolean isClient) {
		TrainType type = lookup(isClient, DEFAULT_ID);
		return type != null ? type : TrainType.FALLBACK;
	}

	public static boolean isMissing(boolean isClient, ResourceLocation id) {
		return lookup(isClient, id) == null;
	}
}
