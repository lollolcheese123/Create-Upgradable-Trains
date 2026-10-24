package com.lollolcheese123.cut.train;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.Optional;

public record TrainType(
		ResourceLocation id,
		float maxSpeedMod,
		float maxTurnSpeedMod,
		float accelMod,
		float decelMod,
		float fuelEffMod,
		float trueFuelMult,
		int tier,
		Optional<String> translatableName,
		String name
) {
	public static final TrainType FALLBACK = new TrainType(
			TrainTypes.DEFAULT_ID,
			1,
			1,
			1,
			1,
			1,
			1,
			-1,
			Optional.of("cut.gui.train_engine.default_type"),
			"Unnamed Train Type"
	);

	public static final Codec<TrainType> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.FLOAT.optionalFieldOf("max_speed_mult", TrainType.FALLBACK.maxSpeedMod()).forGetter(TrainType::maxSpeedMod),
			Codec.FLOAT.optionalFieldOf("max_turn_speed_mult", TrainType.FALLBACK.maxTurnSpeedMod()).forGetter(TrainType::maxTurnSpeedMod),
			Codec.FLOAT.optionalFieldOf("accel_mult", TrainType.FALLBACK.accelMod).forGetter(TrainType::accelMod),
			Codec.FLOAT.optionalFieldOf("decel_mult", TrainType.FALLBACK.decelMod).forGetter(TrainType::decelMod),
			Codec.FLOAT.optionalFieldOf("fuel_eff", TrainType.FALLBACK.fuelEffMod).forGetter(TrainType::fuelEffMod),
			Codec.INT.optionalFieldOf("tier", 1).forGetter(TrainType::tier),
			Codec.STRING.optionalFieldOf("translatable_name").forGetter(TrainType::translatableName),
			Codec.STRING.optionalFieldOf("name", TrainType.FALLBACK.name()).forGetter(TrainType::name)
	).apply(i, (
			speed,
			turnSpeed,
			accel,
			decel,
			fuelEff,
			tier,
			tlName,
			name
	) -> new TrainType(
			TrainTypes.DEFAULT_ID,
			speed,
			turnSpeed,
			accel,
			decel,
			fuelEff,
			1f/fuelEff,
			tier,
			tlName,
			name
	)));

	public static final Codec<TrainType> SEND_CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(TrainType::id),
			Codec.FLOAT.fieldOf("max_speed_mult").forGetter(TrainType::maxSpeedMod),
			Codec.FLOAT.fieldOf("max_turn_speed_mult").forGetter(TrainType::maxTurnSpeedMod),
			Codec.FLOAT.fieldOf("accel_mult").forGetter(TrainType::accelMod),
			Codec.FLOAT.fieldOf("decel_mult").forGetter(TrainType::decelMod),
			Codec.FLOAT.fieldOf("fuel_eff").forGetter(TrainType::fuelEffMod),
			Codec.FLOAT.fieldOf("true_fuel_eff_mod").forGetter(TrainType::trueFuelMult),
			Codec.INT.fieldOf("tier").forGetter(TrainType::tier),
			Codec.STRING.optionalFieldOf("translatable_name").forGetter(TrainType::translatableName),
			Codec.STRING.optionalFieldOf("name", TrainType.FALLBACK.name()).forGetter(TrainType::name)
	).apply(i, TrainType::new));

	public TrainType withId(ResourceLocation id) {
		return new TrainType(
				id,
				maxSpeedMod,
				maxTurnSpeedMod,
				accelMod, decelMod,
				fuelEffMod, trueFuelMult,
				tier,
				translatableName,
				name
		);
	}

	public MutableComponent getName() {
		if (this.translatableName.isPresent()) return Component.translatable(this.translatableName.get());
		return Component.literal(this.name);
	}

	public double score() {
		return maxSpeedMod*maxTurnSpeedMod*accelMod*decelMod*fuelEffMod;
	}

	public static final Comparator<TrainType> RANKING = Comparator.comparingInt(TrainType::tier).thenComparingDouble(TrainType::score);
}
