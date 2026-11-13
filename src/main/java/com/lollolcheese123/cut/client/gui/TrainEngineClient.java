package com.lollolcheese123.cut.client.gui;

import com.lollolcheese123.cut.client.gui.window.TrainEngineScreen;
import com.lollolcheese123.cut.train.engine.EngineHost;
import com.lollolcheese123.cut.train.engine.EngineHosts;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class TrainEngineClient {
	public static void openScreen(EngineTarget target, Player player) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		EngineHost host = EngineHosts.resolve(mc.level, target);
		if (host != null) mc.setScreen(new TrainEngineScreen(host, player));
	}
}
