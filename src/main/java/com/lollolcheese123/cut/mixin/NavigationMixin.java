package com.lollolcheese123.cut.mixin;

import com.lollolcheese123.cut.duck.TrainAddons;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Navigation.class)
public class NavigationMixin {

	@Redirect(method = "tick",
				at = @At(value = "INVOKE",
						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$tickDecel(Train train) {
		return ((TrainAddons) train).CUT$deceleration();
	}

	@Redirect(method = "findNearestApproachable",
				at = @At(value = "INVOKE",
						target = "Lcom/simibubi/create/content/trains/entity/Train;acceleration()F"))
	private float CUT$approachDecel(Train train) {
		return ((TrainAddons) train).CUT$deceleration();
	}
}
