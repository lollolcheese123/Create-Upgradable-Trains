package com.lollolcheese123.cut.client.gui.widget;

import com.lollolcheese123.cut.client.gui.TrainTypeOption;
import com.lollolcheese123.cut.client.gui.helper.Animated;
import com.lollolcheese123.cut.registry.CUTTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class TrainTypeOptionList extends AbstractWidget {
	private static final int ROW_H = CUTTextures.TRAIN_ENGINE_TEXT.getHeight();

	private final List<TrainTypeOption> options;
	private final Consumer<TrainTypeOption> onSelect;
	private final Animated highlightY;
	private final Animated scrollAnim;
	private final int selected;
	private int details;
	private double scroll;

	public TrainTypeOptionList(
			int x, int y, int w, int h,
			List<TrainTypeOption> options,
			int initialSelected,
			Consumer<TrainTypeOption> onSelect
	) {
		super(x, y, w, h, Component.empty());
		this.options = options;
		this.highlightY = new Animated(initialSelected * ROW_H, 250);
		this.scrollAnim = new Animated(0, 120);
		this.selected = initialSelected;
		this.details = initialSelected;
		this.onSelect = onSelect;
	}

	private int maxScroll() { return Math.max(0, options.size() * ROW_H - height + 4); }

	private int indexAt(double mx, double my) {
		if (mx < getX() || mx >= getX() + width || my < getY() || my >= getY() + height) return -1;
		int i = (int) ((my - getY() + Math.round(scrollAnim.get())) / ROW_H);
		return i >= 0 && i < options.size() ? i : -1;
	}

	public TrainTypeOption hovered(int mx, int my) {
		int i = indexAt(mx, my);
		return i < 0 ? null : options.get(i);
	}

	public TrainTypeOption details() {
		return this.options.get(details);
	}

	@Override
	protected void renderWidget(GuiGraphics g, int mx, int my, float pt) {
		Font font = Minecraft.getInstance().font;
		int hover = indexAt(mx, my);
		int scrollPx = (int) Math.round(scrollAnim.get());
		g.enableScissor(getX(), getY(), getX() + width, getY() + height);

		for (int i = 0; i < options.size(); i++) {
			int y = getY() + i * ROW_H - scrollPx;
			if (y + ROW_H < getY() || y > getY() + height) continue;

			if (i == selected)  {
				CUTTextures.TRAIN_ENGINE_TEXT_SELECTED.render(g, getX(), y + 2, -1);
				CUTTextures.TRAIN_ENGINE_SELECTION_ARROW.render(g, getX(), y + 2, 10);
			}
			else CUTTextures.TRAIN_ENGINE_TEXT.render(g, getX(), y + 2, -1);

			if (i == hover) g.fill(getX() + 1, y + 2, getX() + width - 1, y + ROW_H + 2, 0x33FFFFFF);

			g.drawString(font, options.get(i).name(), getX() + 12, y + 8, 0xFFFFFF, true);
		}

		int hy = getY() + (int) Math.round(highlightY.get()) - scrollPx;
		CUTTextures.TRAIN_ENGINE_HOVER_BORDER.render(g, getX(), hy, 5);

		g.disableScissor();
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		int i = indexAt(mx, my);
		if (i < 0 || button != 0) return false;
		details = i;
		highlightY.moveTo(i * ROW_H);
		onSelect.accept(options.get(i));
		playDownSound(Minecraft.getInstance().getSoundManager());
		return true;
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		if (!isMouseOver(mx, my)) return false;
		scroll = Mth.clamp(scroll - scrollY * ROW_H, 0, maxScroll());
		scrollAnim.moveTo(scroll);
		return true;
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput out) {}
}
