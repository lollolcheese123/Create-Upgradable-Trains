package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.client.data.ClientTrainTypes;
import com.lollolcheese123.cut.client.gui.window.TrainEngineScreen;
import com.lollolcheese123.cut.packets.QuotePayload;
import com.lollolcheese123.cut.packets.RequestQuotePayload;
import com.lollolcheese123.cut.packets.SelectOptionPayload;
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
		registrar.playToClient(
				QuotePayload.TYPE,
				QuotePayload.STREAM_CODEC,
				TrainEngineScreen::handleIncomingQuote
		);

		registrar.playToServer(
				SelectOptionPayload.TYPE,
				SelectOptionPayload.STREAM_CODEC,
				SelectOptionPayload::handle
		);
		registrar.playToServer(
				RequestQuotePayload.TYPE,
				RequestQuotePayload.STREAM_CODEC,
				RequestQuotePayload::handle
		);
	}

}
