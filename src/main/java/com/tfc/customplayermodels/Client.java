package com.tfc.customplayermodels;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class Client {
	public static boolean drawing = false;
	
	public static final HashMap<PlayerEntity, Float> capes = new HashMap<>();
	private static final HashMap<PlayerEntity, Float> rotX = new HashMap<>();
	private static final HashMap<PlayerEntity, AnimatedPlayer> animatedPlayers = new HashMap<>();
	
	public static float currentRotBody = 0;
	public static float partialTicks = 0;
	public static PlayerRenderer renderer = null;
	private static String currentModel = "";
	
	public static void onRenderEntity(RenderPlayerEvent event) {
		event.getMatrixStack().push();
		
		if (event instanceof RenderPlayerEvent.Pre) {
			if (!drawing) {
				renderer = event.getRenderer();

//				event.getMatrixStack().translate(0,2,0);
				
				if (!rotX.containsKey(event.getPlayer()))
					rotX.put(event.getPlayer(), event.getPlayer().getPitch(event.getPartialRenderTick()));
				
				if (!capes.containsKey(event.getPlayer())) capes.put(event.getPlayer(), 0f);
				
				if (!animatedPlayers.containsKey(event.getPlayer()))
					animatedPlayers.put(event.getPlayer(), new AnimatedPlayer(event.getPlayer()));
				
				event.setCanceled(event.isCancelable());
				float xRot = rotX.get(event.getPlayer());
				
				if (xRot <= event.getPlayer().getYaw(event.getPartialRenderTick()) - 45)
					xRot = event.getPlayer().getYaw(event.getPartialRenderTick()) - 45;
				
				if (xRot >= event.getPlayer().getYaw(event.getPartialRenderTick()) + 45)
					xRot = event.getPlayer().getYaw(event.getPartialRenderTick()) + 45;
				
				if (event.getPlayer().isSwingInProgress)
					xRot = MathHelper.lerp(0.25f, xRot, event.getPlayer().getYaw(event.getPartialRenderTick()));
				
				if (event.getPlayer().moveForward != 0)
					xRot = MathHelper.lerp(0.1f, xRot, event.getPlayer().getYaw(event.getPartialRenderTick()));
				
				if (event.getPlayer().moveStrafing != 0)
					xRot = MathHelper.lerp(0.1f, xRot, (-45 * event.getPlayer().moveStrafing) + event.getPlayer().getYaw(event.getPartialRenderTick()));
				
				if (event.getPlayer().isPassenger())
					xRot = event.getPlayer().getRidingEntity().getYaw(event.getPartialRenderTick());
				
				if (event.getPlayer().isElytraFlying())
					xRot = event.getPlayer().getYaw(event.getPartialRenderTick());

//				System.out.println(event.getPlayer().moveStrafing);
				rotX.replace(event.getPlayer(), xRot);
				currentRotBody = xRot;
				partialTicks = event.getPartialRenderTick();
				
				try {
					drawing = true;
					
					if (!event.getPlayer().isSleeping()) {
						event.getMatrixStack().rotate(new Quaternion(0, 180 - rotX.get(event.getPlayer()), 0, true));
					} else {
						BlockState state = event.getPlayer().world.getBlockState(event.getEntity().getPosition());
						Direction dir = state.get(BedBlock.HORIZONTAL_FACING);
						event.getMatrixStack().rotate(dir.getRotation());
						event.getMatrixStack().translate(0, -1.5f, 0);
					}
					
					//TODO: minify if statement
					if (AnimatedPlayerGeoRenderer.modelsToLoad.containsKey(new ResourceLocation("cpm", event.getPlayer().getUniqueID().toString()))) {
						if (!AnimatedPlayerGeoRenderer.modelsToLoad.get(new ResourceLocation("cpm", event.getPlayer().getUniqueID().toString())).equals("")) {
							if (AnimatedPlayerGeoRenderer.modelsToLoad.get(new ResourceLocation("cpm", event.getPlayer().getUniqueID().toString())).startsWith("{")) {
								AnimatedPlayerGeoRenderer.INSTANCE.render(
										AnimatedPlayerGeoRenderer.INSTANCE.getGeoModelProvider().getModel((new ResourceLocation("cpm:" + event.getPlayer().getUniqueID().toString()))),
										animatedPlayers.get(event.getPlayer()),
										event.getPartialRenderTick(),
										getRenderType(event.getRenderer(), event.getPlayer(), false, true, false),
										event.getMatrixStack(),
										event.getBuffers(),
										null,
										event.getLight(),
										OverlayTexture.NO_OVERLAY,
										1, 1, 1, 1
								);
							} else
								event.setCanceled(false);
						} else
							event.setCanceled(false);
					} else
						event.setCanceled(false);
					
					drawing = false;
				} catch (Throwable err) {
					err.printStackTrace();
//					System.out.println(event.getEntity().getType());
					drawing = false;
					event.setCanceled(false);
				}
			}
		}
		
		event.getMatrixStack().pop();
	}
	
	@Nullable
	public static RenderType getRenderType(PlayerRenderer renderer, PlayerEntity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
		ResourceLocation resourcelocation = renderer.getEntityTexture((AbstractClientPlayerEntity) p_230496_1_);

//		System.out.println(resourcelocation);

//		if (p_230496_1_.getName().getUnformattedComponentText().equals("Dev"))
//			return RenderType.getEntityTranslucent(new ResourceLocation("minecraft:skins/a002e95d2c83d6f9ee5a9e9ebe03d5e901e1012c"));
		
		if (AnimatedPlayerGeoRenderer.modelsToLoad.containsKey(new ResourceLocation("cpm", p_230496_1_.getUniqueID().toString()))) {
			if (AnimatedPlayerGeoRenderer.modelsToLoad.get(new ResourceLocation("cpm", p_230496_1_.getUniqueID().toString())).contains("\"_texture_\":\"")) {
				String val = AnimatedPlayerGeoRenderer.modelsToLoad.get(new ResourceLocation("cpm", p_230496_1_.getUniqueID().toString()));
				String text = val.split("\"_texture_\":\"")[1];
				text = text.split("\"")[0];
				return RenderType.getEntityCutoutNoCull(new ResourceLocation(text));
			}
		}
		if (p_230496_3_)
			return RenderType.getEntityCutoutNoCull(resourcelocation);
		else if (p_230496_2_)
			return RenderType.getEntityTranslucent(resourcelocation);
		else
			return p_230496_4_ ? RenderType.getEntityTranslucent(resourcelocation, true) : null;
	}
	
	@Nullable
	public static RenderType getRenderTypeCape(PlayerRenderer renderer, PlayerEntity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
		ResourceLocation resourcelocation = ((AbstractClientPlayerEntity) p_230496_1_).getLocationCape();

//		if (p_230496_1_.getName().getUnformattedComponentText().equals("GiantLuigi4"))
//			return RenderType.getEntityTranslucent(new ResourceLocation("cpm:textures/cape/testcape/testcape.png"));
		
		if (resourcelocation != null) {
			if (p_230496_3_)
				return RenderType.getEntityTranslucent(resourcelocation);
			else if (p_230496_2_)
				return RenderType.getEntityTranslucent(resourcelocation);
			else
				return p_230496_4_ ? RenderType.getEntityTranslucent(resourcelocation, true) : null;
		} else {
			return null;
		}
	}
	
	public static void onTick(TickEvent.ClientTickEvent event) {
		try {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().world != null) {
				File f = new File("cpm/models/settings.properties");
				
				if (!f.exists()) {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
				
				PropertiesReader reader = new PropertiesReader(f);
				
				String properties = reader.getValue("ModelPointer");
				InputStream stream = new FileInputStream(new File("cpm/models/" + properties + ".geo.json"));
				byte[] bytes = new byte[stream.available()];
				stream.read(bytes);
				stream.close();
				String newModel = new String(bytes);
				File f1 = new File("cpm/models/" + properties + ".animation.json");
				String anim;
				if (f1.exists()) {
					InputStream stream1 = new FileInputStream(f1);
					byte[] bytes1 = new byte[stream1.available()];
					stream1.read(bytes1);
					stream1.close();
					anim = new String(bytes1);
				} else {
					anim = "{\"format_version\":\"1.8.0\",\"animations\":{\"animation.idle\":{\"loop\":true}}}";
				}
				
				String texture = reader.getValue("TexturePointer");
				if (texture != null) {
					newModel = "{\"_texture_\":\"" + texture + "\"" + newModel.substring(1);
				}
				newModel = newModel.replace(" ", "").replace("\t", "").replace("\n", "");
				anim = anim.replace(" ", "").replace("\t", "").replace("\n", "");
//				currentModel = "";
				
				if (!currentModel.equals(newModel)) {
					CustomPlayerModels.INSTANCE.sendToServer(new ModelPacket(newModel, Minecraft.getInstance().player.getUniqueID(), anim));
					currentModel = newModel;
				}
			}
		} catch (Throwable ignored) {
		}
	}
	
	public static void renderName(AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		double d0 = Client.renderer.getRenderManager().squareDistanceTo(entityIn);
		matrixStackIn.push();
		
		if (d0 < 100.0D) {
			Scoreboard scoreboard = entityIn.getWorldScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
			
			if (scoreobjective != null) {
				Score score = scoreboard.getOrCreateScore(entityIn.getScoreboardName(), scoreobjective);
				superRenderName(entityIn, (new StringTextComponent(Integer.toString(score.getScorePoints()))).appendString(" ").append(scoreobjective.getDisplayName()), matrixStackIn, bufferIn, packedLightIn);
				matrixStackIn.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
			}
		}
		
		superRenderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.pop();
	}
	
	public static void superRenderName(AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		double d0 = Client.renderer.getRenderManager().squareDistanceTo(entityIn);
		
		if (!(d0 > 4096.0D)) {
			boolean flag = !entityIn.isDiscrete();
			float f = entityIn.getHeight() + 0.5F;
			int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
			matrixStackIn.push();
			matrixStackIn.translate(0.0D, (double) f, 0.0D);
			matrixStackIn.rotate(Client.renderer.getRenderManager().getCameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
			float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			FontRenderer fontrenderer = Client.renderer.getFontRendererFromRenderManager();
			float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
			fontrenderer.func_243247_a(displayNameIn, f2, (float) i, 553648127, false, matrix4f, bufferIn, flag, j, packedLightIn);
			
			if (flag)
				fontrenderer.func_243247_a(displayNameIn, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
			
			matrixStackIn.pop();
		}
	}
}
