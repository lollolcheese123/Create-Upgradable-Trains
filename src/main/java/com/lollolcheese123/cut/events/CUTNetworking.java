package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.packets.SyncTrainTypesPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CreateUpgradableTrains.MODID)
public class CUTNetworking {

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(
				SyncTrainTypesPayload.TYPE,
				SyncTrainTypesPayload.STREAM_CODEC,
				(payload, context) -> ClientTrainTypes.set(payload.types())
		);
	}

}
