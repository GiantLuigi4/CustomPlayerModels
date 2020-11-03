package com.tfc.customplayermodels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.util.UUID;

public class AnimatedPlayer implements IAnimatable {
	private final PlayerEntity thisPlayer;
	private final AnimationFactory factory = new AnimationFactory(this);
	
	public AnimatedPlayer(PlayerEntity thisPlayer) {
		this.thisPlayer = thisPlayer;
	}
	
	@Override
	public void registerControllers(AnimationData animationData) {
		AnimationController<AnimatedPlayer> controller = new AnimationController<>(
				this,
				"controller",
				0,
				this::predicate
		);
		controller.addModelFetcher((animatable) -> {
			return new IAnimatableModel() {
				@Override
				public void setLivingAnimations(Object o, Integer integer, AnimationEvent animationEvent) {
				}
				
				@Override
				public AnimationProcessor getAnimationProcessor() {
					return null;
				}
				
				@Override
				public Animation getAnimation(String s, IAnimatable iAnimatable) {
					try {
						return new AnimationFileLoader().loadAllAnimations(new MolangParser(), new ResourceLocation("cpm:animation/" + ((AnimatedPlayer) animatable).getUniqueID().hashCode() + ".animation.json"), AnimatedPlayerGeoRenderer.INSTANCE).getAnimation(s);
					} catch (Throwable ignored) {
						return null;
					}
				}
				
				@Override
				public void setMolangQueries(IAnimatable iAnimatable, double v) {
				}
			};
		});
		animationData.addAnimationController(controller);
	}
	
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
	
	public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.idle", true));
		return PlayState.CONTINUE;
	}
	
	public PlayerEntity getPlayer() {
		return thisPlayer;
	}
	
	public UUID getUniqueID() {
		return thisPlayer.getUniqueID();
	}
	
	public boolean isElytraFlying() {
		return thisPlayer.isElytraFlying();
	}
	
	public float getPitch(float partialTicks) {
		return thisPlayer.getPitch(partialTicks);
	}
	
	public Vector3d getPositionVec() {
		return thisPlayer.getPositionVec();
	}
	
	public ItemStack getHeldItem(Hand mainHand) {
		return thisPlayer.getHeldItem(mainHand);
	}
	
	public ITextComponent getDisplayName() {
		return thisPlayer.getDisplayName();
	}
	
	public boolean isCrouching() {
		return thisPlayer.isCrouching();
	}
	
	public boolean isPassenger() {
		return thisPlayer.isPassenger();
	}
	
	public float getYaw(float partialTicks) {
		return thisPlayer.getYaw(partialTicks);
	}
	
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotType) {
		return thisPlayer.getItemStackFromSlot(slotType);
	}
}
