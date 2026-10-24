package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import com.lollolcheese123.cut.train.TrainTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CreateUpgradableTrains.MODID)
public class CUTDebugCommands {

	@SubscribeEvent
	public static void OnRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(
				Commands.literal("cut")
					.requires(src -> src.hasPermission(2))
					.then(Commands.literal("debug")
						.then(Commands.literal("list")
							.executes(ctx -> list(ctx.getSource())))));
	}

	private static int list(CommandSourceStack src) {
		var types = src.getLevel().isClientSide ?
				ClientTrainTypes.getAll() :
				TrainTypeManager.INSTANCE.all().keySet();

		if (types.isEmpty()) {
			src.sendFailure(Component.literal("No train types loaded"));
			return 0;
		}
		src.sendSuccess(() -> Component.literal("Loaded " + types.size() + " train types:"), false);
		types.forEach(id ->
				src.sendSuccess(() -> Component.literal(" - " + id), false));
		return types.size();
	}
}
