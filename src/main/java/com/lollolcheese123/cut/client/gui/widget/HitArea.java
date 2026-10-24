package com.lollolcheese123.cut.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class HitArea extends AbstractWidget {
	private final Runnable onPress;

	public HitArea(int x, int y, int w, int h, Component tooltip, Runnable onPress) {
		super(x, y, w, h, tooltip);
		this.onPress = onPress;
		setTooltip(Tooltip.create(tooltip));
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		if (!active)
			g.fill(getX(), getY(), getX() + width, getY() + height, 0x99404040);
		else if (isHoveredOrFocused())
			g.fill(getX(), getY(), getX() + width, getY() + height, 0x33FFFFFF);
	}

	@Override
	public void onClick(double mx, double my) {
		onPress.run();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput out) {
		defaultButtonNarrationText(out);
	}
}
