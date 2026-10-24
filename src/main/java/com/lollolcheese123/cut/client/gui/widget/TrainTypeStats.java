package com.lollolcheese123.cut.client.gui.widget;

import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.train.TrainType;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrainTypeStats extends AbstractWidget {
	private static final int R_BARS = 4;
	private static final int Y_BARS = 5;
	private static final int G_BARS = 8;
	private static final int D_BARS = 3;
	private static final int MAX_BAR_CHAR_WIDTH = R_BARS + Y_BARS + G_BARS + D_BARS;
	private static final int NAME_WIDTH = 25;
	private static final float SPACE_WIDTH_MULT = 1f/Minecraft.getInstance().font.width(".");
	private static final float GOGGLE_SIZE_MULT = 2f;
	private static final int SPACING = 2;
	private static final float SCALE = 0.8f;

	private final TrainTypeOptionList optionList;

	public TrainTypeStats(
			int x, int y, int w, int h,
			TrainTypeOptionList optionList
	) {
		super(x, y, w, h, Component.empty());
		this.optionList = optionList;
	}

	@Override
	protected void renderWidget(GuiGraphics g, int i, int i1, float v) {
		Font font = Minecraft.getInstance().font;
		g.enableScissor(getX(), getY(), getX() + width, getY() + height);

		TrainType type = ClientTrainTypes.getOrDefault(optionList.details().id());
		List<Component> info = new ArrayList<>();

		info.add(getRow(false, "cut.gui.train_engine.max_speed_text", type.maxSpeedMod(), ClientTrainTypes.getMaxSpeedMult()));
		info.add(getRow(false, "cut.gui.train_engine.max_turn_speed_text", type.maxTurnSpeedMod(), ClientTrainTypes.getMaxTurnSpeedMult()));
		info.add(getRow(false, "cut.gui.train_engine.accel_text", type.accelMod(), ClientTrainTypes.getMaxAccelMult()));
		info.add(getRow(false, "cut.gui.train_engine.break_text", type.decelMod(), ClientTrainTypes.getMaxDecelMult()));
		info.add(getRow(false, "cut.gui.train_engine.fuel_eff_text", type.fuelEffMod(), ClientTrainTypes.getMaxFuelEff()));

		float height = 0;
		for (Component component : info) {
			drawScaled(g, font, component, getX(), (int) Math.floor(getY() + height + 4), SCALE,0xFFFFFF);
			height += font.lineHeight*SCALE + SPACING;
		}

		g.disableScissor();
	}

	private static void drawScaled(
			GuiGraphics g,
			Font font,
			Component text,
			int x, int y,
			float scale,
			int color) {
		g.pose().pushPose();
		g.pose().translate(x, y, 0);
		g.pose().scale(scale, scale, 1f);
		g.drawString(font, text, 0, 0, color, false);
		g.pose().popPose();
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput out) {}

	private static float getPercent(float val, float maxVal) {
		if (val <= 1f) return 0.5f * Math.max(val, 0);
		if (maxVal <= 1f) return 1f;
		float t = (val - 1f) / (maxVal - 1f);
		return 0.5f + 0.5f * Math.min(t, 1f);
	}

	private static ChatFormatting getColor(float value, float maxValue) {
		int barsCount = Math.round(getPercent(value, maxValue) * MAX_BAR_CHAR_WIDTH);
		if (barsCount <= R_BARS) return ChatFormatting.DARK_RED;
		if (barsCount <= R_BARS + Y_BARS) return ChatFormatting.YELLOW;
		if (barsCount <= R_BARS + Y_BARS + G_BARS) return ChatFormatting.GREEN;
		return ChatFormatting.DARK_GREEN;
	}

	public static Component getRow(boolean forGoggles, String translatableKey, float value, float maxValue) {
		Font font = Minecraft.getInstance().font;
		MutableComponent comp = Component.translatable(translatableKey)
				.withStyle(forGoggles ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY);

		comp = comp.append(Component.literal(
				Strings.repeat('.', Math.max(0, Math.round((NAME_WIDTH - font.width(comp))*SPACE_WIDTH_MULT)))
		).withStyle(forGoggles ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY));

		if (forGoggles) comp = Component.literal(Strings.repeat(' ', 5)).append(comp);

		return comp
				.append(" ")
				.append(barComponent(forGoggles, value, maxValue))
				.append(" ")
				.append(Component.literal(value + "x").withStyle(getColor(value, maxValue)));
	}

	private static MutableComponent barComponent(boolean forGoggles, float value, float maxValue) {
		float goggleMult = forGoggles ? GOGGLE_SIZE_MULT : 1;
		int scaledMaxBars = Math.round(MAX_BAR_CHAR_WIDTH * goggleMult);
		int barsCount = Math.round(getPercent(value, maxValue) * scaledMaxBars);

		int scaledRBars = Math.round(R_BARS * goggleMult);
		int scaledYBars = Math.round(Y_BARS * goggleMult);
		int scaledGBars = Math.round(G_BARS * goggleMult);
		int scaledDBars = Math.round(D_BARS * goggleMult);


		return Component.empty()
				.append(bars(Math.min(barsCount, scaledRBars), ChatFormatting.DARK_RED))
				.append(bars(Math.min(barsCount - scaledRBars, scaledYBars), ChatFormatting.YELLOW))
				.append(bars(Math.min(barsCount - scaledRBars - scaledYBars, scaledGBars), ChatFormatting.GREEN))
				.append(bars(Math.min(barsCount - scaledRBars - scaledYBars - scaledGBars, scaledDBars), ChatFormatting.DARK_GREEN))
				.append(bars(scaledMaxBars - barsCount, forGoggles ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY));
	}

	private static MutableComponent bars(int count, ChatFormatting format) {
		return Component.literal(Strings.repeat('|', Math.max(count, 0)))
				.withStyle(format);
	}
}
