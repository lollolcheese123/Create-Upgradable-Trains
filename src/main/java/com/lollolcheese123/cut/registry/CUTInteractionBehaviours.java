package com.lollolcheese123.cut.registry;

import com.lollolcheese123.cut.contraption.TrainEngineInteraction;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;

public class CUTInteractionBehaviours {
	public static void register() {
		MovingInteractionBehaviour.REGISTRY.register(
				CUTBlocks.TRAIN_ENGINE.get(),
				new TrainEngineInteraction()
		);
	}
}
