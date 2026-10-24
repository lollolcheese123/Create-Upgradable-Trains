package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum CUTTextures implements ScreenElement {
	TRAIN_ENGINE_BG("train_engine_bg", 239, 220),
	TRAIN_ENGINE_HOVER_BORDER("train_engine_options", 0, 0, 110, 24, 110, 84),
	TRAIN_ENGINE_TEXT("train_engine_options", 0, 24, 110, 20, 110, 83),
	TRAIN_ENGINE_TEXT_SELECTED("train_engine_options", 0, 44, 110, 20, 110, 84),
	TRAIN_ENGINE_SELECTION_ARROW("train_engine_options", 0, 64, 110,20, 110, 84),
	;

	public final ResourceLocation location;
	private final int startX, startY, width, height, sheetW, sheetH;

	CUTTextures(String name, int width, int height) {
		this(name, 0, 0, width, height, width, height);
	}

	CUTTextures(String name, int startX, int startY, int width, int height, int sheetW, int sheetH) {
		this.location = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, "textures/gui/" + name + ".png");
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.sheetW = sheetW;
		this.sheetH = sheetH;
	}

	public int getStartX() {
		return this.startX;
	}
	public int getStartY() {
		return this.startY;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public int getSheetW() {
		return this.sheetW;
	}
	public int getSheetH() {
		return this.sheetH;
	}

	@Override
	public void render(GuiGraphics g, int x, int y) {
		g.blit(location, x, y, startX, startY, width, height, sheetW, sheetH);
	}
	public void render(GuiGraphics g, int x, int y, int z) {
		g.blit(location, x, y, z, startX, startY, width, height, sheetW, sheetH);
	}
}
