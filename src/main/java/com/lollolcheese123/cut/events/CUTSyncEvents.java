package com.lollolcheese123.cut.events;

import com.lollolcheese123.cut.CreateUpgradableTrains;
import com.lollolcheese123.cut.packets.SyncTrainTypesPayload;
import com.lollolcheese123.cut.server.data.TrainTypeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreateUpgradableTrains.MODID)
public class CUTSyncEvents {

	@SubscribeEvent
	public static void onDataPackSync(OnDatapackSyncEvent event) {
		SyncTrainTypesPayload payload = new SyncTrainTypesPayload(TrainTypeManager.INSTANCE.all());
		event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
	}

}
