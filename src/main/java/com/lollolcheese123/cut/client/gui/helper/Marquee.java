package com.lollolcheese123.cut.client.gui.helper;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class Marquee {
	private static final int PAUSE_MS = 1000;
	private static final double SPEED = 10;

	private Marquee() {}

	public static int offset(int overflow) {
		if (overflow <= 0) return 0;
		double travel = overflow / SPEED * 1000.0;
		double cycle = 2 * (travel + PAUSE_MS);
		double t = Util.getMillis() % (long) cycle;

		if (t < PAUSE_MS) return 0;
		t -= PAUSE_MS;
		if (t < travel) return (int) (overflow * (t / travel));
		t -= travel;
		if (t < PAUSE_MS) return overflow;
		t -= PAUSE_MS;
		return (int) (overflow * (1 - t / travel));
	}

	public static void draw(GuiGraphics g, Font font, Component text,
	                        int x, int y, int maxW, float scale, int color) {
		int textW = font.width(text);
		int availLocal = (int) (maxW / scale);
		int overflow = textW - availLocal;

		g.pose().pushPose();
		g.pose().translate(x, y, 0);
		g.pose().scale(scale, scale, 1f);

		if (overflow <= 0) {
			g.drawString(font, text, 0, 0, color, false);
		} else {
			g.enableScissor(x, y, x + maxW, y + (int) Math.ceil(font.lineHeight * scale));
			g.drawString(font, text, -offset(overflow), 0, color, false);
			g.disableScissor();
		}

		g.pose().popPose();
	}
}
