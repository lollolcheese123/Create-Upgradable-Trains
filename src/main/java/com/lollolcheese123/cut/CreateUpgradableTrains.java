package com.lollolcheese123.cut;

import com.lollolcheese123.cut.registry.CUTBlockEntities;
import com.lollolcheese123.cut.registry.CUTBlocks;
import com.lollolcheese123.cut.registry.CUTItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CreateUpgradableTrains.MODID)
public class CreateUpgradableTrains {
	public static final String MODID = "cut";
	public static final Logger LOGGER = LogUtils.getLogger();

	public CreateUpgradableTrains(IEventBus modEventBus, ModContainer modContainer) {
		CUTBlocks.register(modEventBus);
		CUTItems.register(modEventBus);
		CUTBlockEntities.register(modEventBus);
	}
}
