package com.tfc.customplayermodels;

import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

public class IAnimatedPlayer implements IAnimatable {
	private final PlayerEntity thisPlayer;
	
	public IAnimatedPlayer(PlayerEntity thisPlayer) {
		this.thisPlayer = thisPlayer;
	}
	
	@Override
	public void registerControllers(AnimationData animationData) {
	}
	
	@Override
	public AnimationFactory getFactory() {
		return new AnimationFactory(this);
	}
}
