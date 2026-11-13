package com.lollolcheese123.cut.train.engine;

import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TrainEngineOptions {
	public static List<TrainTypeOption> get(boolean client) {
		Collection<TrainType> source = client ? ClientTrainTypes.getAll() : TrainTypeManager.INSTANCE.all().values();
		List<TrainTypeOption> list = new ArrayList<>();

		boolean defaultLoaded = false;
		for (TrainType trainType : source) {
			Component name = trainType.translatableName().isPresent()
					? Component.translatable(trainType.translatableName().get())
					: Component.literal(trainType.name());
			list.add(new TrainTypeOption(trainType.tier(), trainType.score(), trainType.id(), name, List.of(), List.of()));
			if (trainType.id().equals(TrainTypes.DEFAULT_ID)) defaultLoaded = true;
		}

		if (!defaultLoaded)
			list.add(new TrainTypeOption(
					TrainType.FALLBACK.tier(), TrainType.FALLBACK.score(), TrainTypes.DEFAULT_ID,
					Component.translatable(TrainType.FALLBACK.translatableName().orElse("cut.gui.train_engine.default_type")),
					List.of(), List.of()
			));

		list.sort(TrainTypeOption::compareTo);
		return list;
	}
}
