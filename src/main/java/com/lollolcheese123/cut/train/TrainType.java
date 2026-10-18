package com.lollolcheese123.cut.train;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Comparator;

public record TrainType(
		float maxSpeedMod,
		float maxTurnSpeedMod,
		float accelMod,
		float decelMod,
		int tier
) {
	public static final Codec<TrainType> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.FLOAT.fieldOf("max_speed_mult").forGetter(TrainType::maxSpeedMod),
			Codec.FLOAT.fieldOf("max_turn_speed_mult").forGetter(TrainType::maxTurnSpeedMod),
			Codec.FLOAT.fieldOf("accel_mult").forGetter(TrainType::accelMod),
			Codec.FLOAT.fieldOf("decel_mult").forGetter(TrainType::decelMod),
			Codec.INT.fieldOf("tier").forGetter(TrainType::tier)
	).apply(i, TrainType::new));

	public double score() {
		return maxSpeedMod*maxTurnSpeedMod*accelMod*decelMod;
	}

	public static final Comparator<TrainType> RANKING = Comparator.comparingInt(TrainType::tier).thenComparingDouble(TrainType::score);

	public static final TrainType FALLBACK = new TrainType(
			1, 1, 1, 1, -1
	);
}
