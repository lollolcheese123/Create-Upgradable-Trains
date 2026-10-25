package com.lollolcheese123.cut.client.gui;

import com.lollolcheese123.cut.blocks.blockentity.TrainEngineBlockEntity;
import com.lollolcheese123.cut.client.gui.window.TrainEngineScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class TrainEngineClient {
	public static void openScreen(BlockPos pos, Player player) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getBlockEntity(pos) instanceof TrainEngineBlockEntity be)
			mc.setScreen(new TrainEngineScreen(be, player));
	}
}
