package com.lollolcheese123.cut.client.gui.helper;

import net.minecraft.Util;
import net.minecraft.util.Mth;

public class Animated {
	private final float durationMSmult;
	private double from, to;
	private long start;

	public Animated(double value, long durationMS) {
		this.from = this.to = value;
		this.durationMSmult = 1/(float)durationMS;
	}

	public void moveTo(double target) {
		if (target == to) return;
		from = get();
		to = target;
		start = Util.getMillis();
	}

	public double get() {
		float t = Mth.clamp((Util.getMillis() - start) * durationMSmult, 0f, 1f);
		float eased = 1f - (1f - t) * (1f - t) * (1f - t);
		return Mth.lerp(eased, from, to);
	}
}
