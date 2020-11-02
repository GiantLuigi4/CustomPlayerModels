package com.tfc.customplayermodels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

import java.util.UUID;

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
