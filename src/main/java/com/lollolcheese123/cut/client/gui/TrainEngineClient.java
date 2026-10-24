package com.lollolcheese123.cut.client.gui;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.window.TrainEngineScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class TrainEngineClient {
	public static void openScreen(BlockPos pos) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getBlockEntity(pos) instanceof TrainEngineBlockEntity be)
			mc.setScreen(new TrainEngineScreen(be));
	}
}
