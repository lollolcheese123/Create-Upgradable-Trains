package com.lollolcheese123.cut.duck;

import net.minecraft.resources.ResourceLocation;

public interface TrainAddons {
	ResourceLocation CUT$getTrainTypeId();
	void CUT$setTrainTypeId(ResourceLocation resourceLocation);

	float CUT$deceleration();

	boolean CUT$isClientSide();
}
