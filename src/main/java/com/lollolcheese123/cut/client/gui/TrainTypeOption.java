package com.lollolcheese123.cut.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TrainTypeOption (
		int tier,
		double score,
		ResourceLocation id,
		Component name,
		List<Component> hoverInfo,
		List<Component> details
) implements Comparable<TrainTypeOption> {
	@Override
	public int compareTo(@NotNull TrainTypeOption other) {
		if (this.tier != other.tier) return Integer.compare(this.tier, other.tier);
		return Double.compare(this.score, other.score);
	}
}
