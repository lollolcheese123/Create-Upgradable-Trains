package com.lollolcheese123.cut.server.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
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
		if (!types.isEmpty())
			CreateUpgradableTrains.LOGGER.info("TrainTypeManager apply: Loaded {} Train Types from {} files!", types.size(), files.size());
		else {
			CreateUpgradableTrains.LOGGER.info("TrainTypeManager apply: No Train Types found in {} files, loading default preset instead!", files.size());
			loadDefaultPreset();
		}
	}

	public @Nullable TrainType get(ResourceLocation id) {
		return types.get(id);
	}
	public Map<ResourceLocation, TrainType> all() {
		return types;
	}

	private void loadDefaultPreset() {
		Map<ResourceLocation, TrainType> newTypes = new HashMap<>();

		newTypes.put(
			TrainTypes.DEFAULT_ID,
			TrainType.create(
					TrainTypes.DEFAULT_ID,
					1f,
					1f,
					1f,
					1f,
					1f,
					1,
					"cut.train_types.default.default",
					"Default Train",
					Map.of(),
					0
			)
		);

		ResourceLocation sprinterId = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID,
				"sprinter");
		newTypes.put(
			sprinterId,
			TrainType.create(
					sprinterId,
					0.8f,
					0.8f,
					2f,
					2.5f,
					0.9f,
					2,
					"cut.train_types.default.sprinter",
					"Sprinter",
					Map.of(),
					0
			)
		);

		ResourceLocation intercityId = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID,
				"intercity");
		newTypes.put(
			intercityId,
			TrainType.create(
					intercityId,
					2,
					1.5f,
					0.85f,
					1.25f,
					0.8f,
					2,
					"cut.train_types.default.intercity",
					"Intercity",
					Map.of(),
					0
			)
		);

		ResourceLocation ecoTrainId = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID,
				"eco_train");
		newTypes.put(
			ecoTrainId,
			TrainType.create(
					ecoTrainId,
					0.9f,
					0.9f,
					0.9f,
					1,
					2,
					1,
					"cut.train_types.default.eco_train",
					"Eco-Train",
					Map.of(),
					0
			)
		);

		ResourceLocation expressTrainId = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID,
				"express_train");
		newTypes.put(
			expressTrainId,
			TrainType.create(
					expressTrainId,
					5,
					3.5f,
					0.75f,
					2,
					0.7f,
					3,
					"cut.train_types.default.international",
					"Express Train",
					Map.of(),
					0
			)
		);

		this.types = Map.copyOf(newTypes);
	}
}
