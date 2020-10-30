package com.tfc.customplayermodels;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.client.event.RenderPlayerEvent;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Client {
	public static boolean drawing = false;
	
	private static final HashMap<PlayerEntity,Float> rotX = new HashMap<>();
	
	public static float currentRotBody = 0;
	public static float partialTicks = 0;
	
	public static void onRenderEntity(RenderPlayerEvent event) {
		event.getMatrixStack().push();
		
		if (event instanceof RenderPlayerEvent.Pre) {
			if (!drawing) {

				if (!rotX.containsKey(event.getPlayer())) rotX.put(event.getPlayer(),event.getPlayer().getPitch(event.getPartialRenderTick()));

				event.setCanceled(event.isCancelable());
				float xRot = rotX.get(event.getPlayer());

				if (xRot <= event.getPlayer().getYaw(event.getPartialRenderTick()) - 45)
					xRot = event.getPlayer().getYaw(event.getPartialRenderTick()) - 45;
				if (xRot >= event.getPlayer().getYaw(event.getPartialRenderTick()) + 45)
					xRot = event.getPlayer().getYaw(event.getPartialRenderTick()) + 45;
				if (event.getPlayer().isSwingInProgress)
					xRot = MathHelper.lerp(0.25f,xRot,event.getPlayer().getYaw(event.getPartialRenderTick()));
				if (event.getPlayer().moveForward != 0) xRot = MathHelper.lerp(0.1f, xRot, event.getPlayer().getYaw(event.getPartialRenderTick()));
				if (event.getPlayer().moveStrafing != 0) xRot = MathHelper.lerp(0.1f, xRot, (-45*event.getPlayer().moveStrafing)+event.getPlayer().getYaw(event.getPartialRenderTick()));
				
//				System.out.println(event.getPlayer().moveStrafing);
				rotX.replace(event.getPlayer(),xRot);
				currentRotBody = xRot;
				partialTicks = event.getPartialRenderTick();
				
				try {
					drawing = true;
					event.getMatrixStack().rotate(new Quaternion(0,180-rotX.get(event.getPlayer()),0,true));
					AnimatedPlayerGeoRenderer.INSTANCE.render(
							AnimatedPlayerGeoRenderer.INSTANCE.getGeoModelProvider().getModel((new ResourceLocation("cpm:"+event.getPlayer().getDisplayName().getUnformattedComponentText().toLowerCase()))),
							event.getPlayer(),
							event.getPartialRenderTick(),
							func_230496_a_(event.getRenderer(),event.getPlayer(),false,true,false),
							event.getMatrixStack(),
							event.getBuffers(),
							null,
							event.getLight(),
							OverlayTexture.NO_OVERLAY,
							1,1,1,1
							);
					drawing = false;
				} catch (Throwable ignored) {
					ignored.printStackTrace();
					System.out.println(event.getEntity().getType());
					drawing = false;
				}
			}
		}
		
		event.getMatrixStack().pop();
	}
	
	@Nullable
	protected static RenderType func_230496_a_(PlayerRenderer renderer, PlayerEntity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
		ResourceLocation resourcelocation = renderer.getEntityTexture((AbstractClientPlayerEntity) p_230496_1_);

		if (p_230496_3_) {
			return RenderType.getItemEntityTranslucentCull(resourcelocation);
		} else if (p_230496_2_) {
			return RenderType.getEntitySolid(resourcelocation);
		} else {
			return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
		}
	}
}
