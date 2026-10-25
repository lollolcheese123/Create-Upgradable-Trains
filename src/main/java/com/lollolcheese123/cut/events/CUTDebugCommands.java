package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import com.lollolcheese123.cut.train.TrainType;
import com.lollolcheese123.cut.train.TrainTypes;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = CreateUpgradableTrains.MODID)
public class CUTDebugCommands {

	@SubscribeEvent
	public static void OnRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(
				Commands.literal("cut")
						.requires(src -> src.hasPermission(2))
						.then(Commands.literal("debug")
								.then(Commands.literal("list")
										.executes(ctx -> list(ctx.getSource()))
								)
								.then(Commands.literal("get")
										.then(Commands.argument("type", StringArgumentType.string())
												.suggests(((ctx, builder) -> SharedSuggestionProvider.suggest(getTypes(ctx.getSource()), builder)))
												.executes(context -> {
													String chosenType = StringArgumentType.getString(context, "type");

													return getDetails(context.getSource(), chosenType);
												})
										)
								)
						)
		);




//		event.getDispatcher().register(
//				Commands.literal("cut")
//					.requires(src -> src.hasPermission(2))
//					.then(Commands.literal("debug")
//						.then(Commands.literal("list")
//							.executes(ctx -> list(ctx.getSource()))
//						.then(Commands.literal("get")))));
	}

	private static int list(CommandSourceStack src) {
		var types = src.getLevel().isClientSide ?
				ClientTrainTypes.getAll() :
				TrainTypeManager.INSTANCE.all().values();

		if (types.isEmpty()) {
			src.sendFailure(Component.literal("No train types loaded"));
			return 0;
		}
		src.sendSuccess(() -> Component.literal("Loaded " + types.size() + " train types:"), false);
		types.forEach(id ->
				src.sendSuccess(() -> Component.literal(" - " + id), false));
		return types.size();
	}

	private static List<String> getTypes(CommandSourceStack src) {
		Collection<TrainType> types = src.getLevel().isClientSide ?
				ClientTrainTypes.getAll() :
				TrainTypeManager.INSTANCE.all().values();

		List<String> out = new ArrayList<>();
		for (TrainType type : types) out.add(type.id().getPath());
		out.sort(String::compareTo);

		return out;
	}

	private static int getDetails(CommandSourceStack src, String stringTypeId) {
		ResourceLocation typeId = ResourceLocation.fromNamespaceAndPath(CreateUpgradableTrains.MODID, stringTypeId);

		if (TrainTypes.isMissing(src.getLevel().isClientSide, typeId)) {
			src.sendFailure(Component.literal("Invalid train type"));
			return 0;
		}

		TrainType type = TrainTypes.lookupOrFallback(src.getLevel().isClientSide, typeId);

		src.sendSuccess(() -> Component.literal(
				"id: " + type.id().toString() + "\n" +
				"max_speed_mult: " + type.maxSpeedMod() + "\n" +
				"max_turn_speed_mult: " + type.maxTurnSpeedMod() + "\n" +
				"accel_mult: " + type.accelMod() + "\n" +
				"decel_mult: " + type.decelMod() + "\n" +
				"fuel_eff: " + type.fuelEffMod() + "\n" +
				"true_fuel_eff_mod: " + type.trueFuelMult() + "\n" +
				"tier: " + type.tier() + "\n" +
				"translatable_name: " + (type.translatableName().isPresent() ? type.translatableName().get() : "N/A") + "\n" +
				"name: " + type.name() + "\n" +
				"money_cost: " + type.moneyCost() + "\n" +
				"item_cost: " + type.itemCost().toString() + "\n"
		), false);
		return 1;
	}
}
