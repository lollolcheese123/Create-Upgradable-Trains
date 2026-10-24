package com.lollolcheese123.cut.server.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.train.TrainType;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TrainTypeManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new Gson();
	public static final TrainTypeManager INSTANCE = new TrainTypeManager();

	private volatile Map<ResourceLocation, TrainType> types = Map.of();

	private TrainTypeManager() {
		super(GSON, "train_types");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> files,
	                     @NotNull ResourceManager rm,
	                     @NotNull ProfilerFiller profiler) {
		Map<ResourceLocation, TrainType> loaded = new HashMap<>();
		files   .forEach((id, json) -> TrainType.CODEC.parse(JsonOps.INSTANCE, json)
				.resultOrPartial(err -> CreateUpgradableTrains.LOGGER.error("Bad train type {}: {}", id, err))
				.ifPresent(t -> loaded.put(id, t.withId(id))));
		types = Map.copyOf(loaded);
		CreateUpgradableTrains.LOGGER.info("TrainTypeManager apply: Loaded {} Train Types from {} files!", types.size(), files.size());
	}

	public @Nullable TrainType get(ResourceLocation id) {
		return types.get(id);
	}
	public Map<ResourceLocation, TrainType> all() {
		return types;
	}
}
