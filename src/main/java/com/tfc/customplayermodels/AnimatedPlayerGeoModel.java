package com.tfc.customplayermodels;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class AnimatedPlayerGeoModel extends AnimatedGeoModel<AnimatedPlayer> {
	private Animation anim;
	private GeoModel model;
	
	@Override
	public ResourceLocation getModelLocation(AnimatedPlayer animatedPlayer) {
		return null;
	}
	
	@Override
	public ResourceLocation getTextureLocation(AnimatedPlayer animatedPlayer) {
		return null;
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(AnimatedPlayer animatedPlayer) {
		return null;
	}
	
	@Override
	public Animation getAnimation(String name, IAnimatable animatable) {
		return anim;
	}
	
	@Override
	public GeoModel getModel(ResourceLocation location) {
		return model;
	}
	
	public void setAnimation(Animation anim) {
		this.anim = anim;
	}
	
	public void setModel(GeoModel model) {
		this.model = model;
	}
}
