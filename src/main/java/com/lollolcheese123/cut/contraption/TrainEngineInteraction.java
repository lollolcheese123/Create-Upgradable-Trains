package com.lollolcheese123.cut.contraption;

import com.lollolcheese123.cut.client.gui.TrainEngineClient;
import com.lollolcheese123.cut.train.engine.EngineTarget;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class TrainEngineInteraction extends MovingInteractionBehaviour {
	@Override
	public boolean handlePlayerInteraction(
			Player player,
			InteractionHand hand,
			BlockPos localPos,
			AbstractContraptionEntity entity
	) {
		if (player.level().isClientSide)
			TrainEngineClient.openScreen(new EngineTarget.OnContraption(entity.getId(), localPos), player);
		return true;
	}
}
