package com.tfc.customplayermodels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.*;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.renderers.geo.IGeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AnimatedPlayerGeoRenderer<T extends PlayerEntity> implements IGeoRenderer<T>, IResourceManager {
	private static final GeoModelLoader loader = new GeoModelLoader();
	
	public static final AnimatedPlayerGeoRenderer<PlayerEntity> INSTANCE = new AnimatedPlayerGeoRenderer<>();
	
	public static final HashMap<ResourceLocation, GeoModel> models = new HashMap<>();
	public static final HashMap<ResourceLocation, String> modelsToLoad = new HashMap<>();
	
	private final GeoModelProvider<T> provider = new GeoModelProvider<T>() {
		@Override
		public ResourceLocation getModelLocation(T t) {
			return new ResourceLocation("cpm:" + t.getUniqueID().toString());
		}
		
		@Override
		public ResourceLocation getTextureLocation(T t) {
			return ((AbstractClientPlayerEntity) t).getLocationSkin();
		}
		
		@Override
		public GeoModel getModel(ResourceLocation location) {
			if (modelsToLoad.containsKey(location)) models.replace(location, loader.loadModel(INSTANCE, location));
			if (!models.containsKey(location)) models.put(location, loader.loadModel(INSTANCE, location));
			return models.get(location);
		}
	};
	
	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return provider;
	}
	
	@Override
	public ResourceLocation getTextureLocation(T t) {
		return provider.getTextureLocation(t);
	}
	
	@Override
	public Set<String> getResourceNamespaces() {
		return ImmutableSet.of("cpm");
	}
	
	@Override
	public IResource getResource(ResourceLocation resourceLocationIn) {
		return new IResource() {
			ArrayList<InputStream> streams = new ArrayList<>();
			
			@Override
			public ResourceLocation getLocation() {
				return resourceLocationIn;
			}
			
			@Override
			public InputStream getInputStream() {
//				InputStream stream = new ByteArrayInputStream(("{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n\t\t\t\t\"identifier\": \"geometry.unknown\",\n\t\t\t\t\"texture_width\": 64,\n\t\t\t\t\"texture_height\": 64,\n\t\t\t\t\"visible_bounds_width\": 4,\n\t\t\t\t\"visible_bounds_height\": 4.5,\n\t\t\t\t\"visible_bounds_offset\": [0, 1.75, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 22, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-8, 15, -8], \"size\": [16, 14, 16], \"uv\": [0, 34]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"cape_handle2\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 28, 13.5]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body2\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 29, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-5, 29, -5], \"size\": [10, 10, 10], \"uv\": [0, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"cape_handle\",\n\t\t\t\t\t\"parent\": \"body2\",\n\t\t\t\t\t\"pivot\": [0, 38, 7.5]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"head\",\n\t\t\t\t\t\"parent\": \"body2\",\n\t\t\t\t\t\"pivot\": [0, 39, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 39, -4], \"size\": [8, 8, 8], \"uv\": [32, 18]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_arm\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [8, 28, 0],\n\t\t\t\t\t\"rotation\": [0, 0, 45],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [8, 27, -1], \"size\": [12, 2, 2], \"uv\": [30, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_arm\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [-8, 28, 0],\n\t\t\t\t\t\"rotation\": [0, 0, -45],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-20, 27, -1], \"size\": [12, 2, 2], \"uv\": [30, 0], \"mirror\": true}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_leg\",\n\t\t\t\t\t\"pivot\": [4, 15, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [2, 0, -2], \"size\": [4, 17, 4], \"uv\": [0, 29]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_leg\",\n\t\t\t\t\t\"pivot\": [-4, 15, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-6, 0, -2], \"size\": [4, 17, 4], \"uv\": [0, 29], \"mirror\": true}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}").getBytes());
//				InputStream stream = new ByteArrayInputStream(("{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n\t\t\t\t\"identifier\": \"geometry.unknown\",\n\t\t\t\t\"texture_width\": 64,\n\t\t\t\t\"texture_height\": 64,\n\t\t\t\t\"visible_bounds_width\": 3,\n\t\t\t\t\"visible_bounds_height\": 4.5,\n\t\t\t\t\"visible_bounds_offset\": [0, 1.75, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"standing\",\n\t\t\t\t\t\"pivot\": [0, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body\",\n\t\t\t\t\t\"parent\": \"standing\",\n\t\t\t\t\t\"pivot\": [0, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"head\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 24, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"uv\": [0, 0]},\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"inflate\": 0.5, \"uv\": [32, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_head\",\n\t\t\t\t\t\"parent\": \"head\",\n\t\t\t\t\t\"pivot\": [0, 24, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"torso\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 18.5, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"uv\": [16, 16]},\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"inflate\": 0.25, \"uv\": [16, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"cape_handle\",\n\t\t\t\t\t\"parent\": \"torso\",\n\t\t\t\t\t\"pivot\": [0, 23, 0.9]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_chest\",\n\t\t\t\t\t\"parent\": \"torso\",\n\t\t\t\t\t\"pivot\": [0, 18.5, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"arms\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [-3, 22.5, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_arm\",\n\t\t\t\t\t\"parent\": \"arms\",\n\t\t\t\t\t\"pivot\": [-3, 22.5, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"uv\": [40, 16]},\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [40, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_r\",\n\t\t\t\t\t\"parent\": \"right_arm\",\n\t\t\t\t\t\"pivot\": [-6, 12, -2],\n\t\t\t\t\t\"rotation\": [90, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_arm\",\n\t\t\t\t\t\"parent\": \"arms\",\n\t\t\t\t\t\"pivot\": [3, 22.5, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"uv\": [32, 48]},\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [48, 48]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_l\",\n\t\t\t\t\t\"parent\": \"left_arm\",\n\t\t\t\t\t\"pivot\": [6, 21, -2],\n\t\t\t\t\t\"rotation\": [90, 0, 180]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"legs\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 12.25, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_feet\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [0, 12, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_pants\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [0, 12, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_leg\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [-2, 12.25, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 32]},\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"uv\": [0, 16]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_leg\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [2, 12.25, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"uv\": [16, 48]},\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 48]}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}").getBytes());
//				InputStream stream = new ByteArrayInputStream(("{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.unknown\",\"texture_width\":64,\"texture_height\":64,\"visible_bounds_width\":5,\"visible_bounds_height\":1.5,\"visible_bounds_offset\":[0,0.25,0]},\"bones\":[{\"name\":\"model\",\"pivot\":[0,0,-2]},{\"name\":\"body\",\"parent\":\"model\",\"pivot\":[-1,3.5,-5],\"rotation\":[-5,0,0],\"cubes\":[{\"origin\":[-3,0.5,-5],\"size\":[5,3,9],\"uv\":[0,8]}]},{\"name\":\"cape_handle\",\"parent\":\"body\",\"pivot\":[-0.5,2.5,-4.25],\"rotation\":[87.5,0,0]},{\"name\":\"wing_l_pt_1\",\"parent\":\"body\",\"pivot\":[-3,3.5,-0.5],\"cubes\":[{\"origin\":[-9,1.5,-5],\"size\":[6,2,9],\"uv\":[23,12],\"mirror\":true}]},{\"name\":\"wing_l_pt_2\",\"parent\":\"wing_l_pt_1\",\"pivot\":[-9,3.5,-0.5],\"cubes\":[{\"origin\":[-22,2.5,-5],\"size\":[13,1,9],\"uv\":[16,24],\"mirror\":true}]},{\"name\":\"equipment_handle_l\",\"parent\":\"wing_l_pt_2\",\"pivot\":[-20,1,-1],\"rotation\":[90,0,0]},{\"name\":\"wing_r_pt_1\",\"parent\":\"body\",\"pivot\":[2,3.5,-0.5],\"cubes\":[{\"origin\":[2,1.5,-5],\"size\":[6,2,9],\"uv\":[23,12]}]},{\"name\":\"wing_r_pt_2\",\"parent\":\"wing_r_pt_1\",\"pivot\":[8,3.5,-0.5],\"cubes\":[{\"origin\":[8,2.5,-5],\"size\":[13,1,9],\"uv\":[16,24]}]},{\"name\":\"equipment_handle_r\",\"parent\":\"wing_r_pt_2\",\"pivot\":[20,1,-1],\"rotation\":[90,0,0]},{\"name\":\"tail1\",\"parent\":\"body\",\"pivot\":[-0.5,3.52619,3.93784],\"cubes\":[{\"origin\":[-2,1.5,3.94396],\"size\":[3,2,6],\"uv\":[3,20]}]},{\"name\":\"tail2\",\"parent\":\"tail1\",\"pivot\":[-0.5,3.30198,9.98038],\"cubes\":[{\"origin\":[-1,2.27579,9.9865],\"size\":[1,1,6],\"uv\":[4,29]}]},{\"name\":\"head\",\"parent\":\"body\",\"pivot\":[-0.5,1.0982,-4.37736],\"rotation\":[10,0,0],\"cubes\":[{\"origin\":[-4,0.09536,-9.31199],\"size\":[7,3,5],\"uv\":[0,0]}]}]}]}").getBytes());
				InputStream stream = new ByteArrayInputStream((modelsToLoad.get(resourceLocationIn)).getBytes());
				streams.add(stream);
				return stream;
			}
			
			@Nullable
			@Override
			public <T> T getMetadata(IMetadataSectionSerializer<T> serializer) {
				return null;
			}
			
			@Override
			public String getPackName() {
				return "null";
			}
			
			@Override
			public void close() throws IOException {
				for (InputStream stream : streams) stream.close();
			}
		};
	}
	
	@Override
	public boolean hasResource(ResourceLocation path) {
		return true;
	}
	
	@Override
	public List<IResource> getAllResources(ResourceLocation resourceLocationIn) throws IOException {
		return ImmutableList.of();
	}
	
	@Override
	public Collection<ResourceLocation> getAllResourceLocations(String pathIn, Predicate<String> filter) {
		return new ArrayList<>();
	}
	
	@Override
	public Stream<IResourcePack> getResourcePackStream() {
		return null;
	}
	
	public static ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, EquipmentSlotType slot, String type) {
		if (stack.getItem() instanceof ArmorItem) {
			ArmorItem item = (ArmorItem) stack.getItem();
			String itemName = item.getArmorMaterial().getName();
			String loc = item.getArmorTexture(stack, entity, slot, type);
			
			if (loc == null) {
				if (slot.equals(EquipmentSlotType.FEET) || slot.equals(EquipmentSlotType.LEGS)) {
					if (type == null)
						return new ResourceLocation("minecraft:textures/models/armor/" + itemName + "_layer_2.png");
					else
						return new ResourceLocation("minecraft:textures/models/armor/" + itemName + "_layer_2" + type + ".png");
				} else if (type == null)
					return new ResourceLocation("minecraft:textures/models/armor/" + itemName + "_layer_1.png");
				else
					return new ResourceLocation("minecraft:textures/models/armor/" + itemName + "_layer_1" + type + ".png");
			} else return new ResourceLocation(loc);
		}
		
		return null;
	}
	
	public void renderLate(GeoModel model, T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		stackIn.push();
		
		for (GeoBone group : model.topLevelBones)
			renderLateBones(group, animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		
		stackIn.pop();
	}
	
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrixStackIn.push();
		
		if (animatable.isElytraFlying()) {
//			matrixStackIn.rotate(new Quaternion(0,-180+animatable.getYaw(partialTicks),0,true));
			matrixStackIn.rotate(new Quaternion(-90 - animatable.getPitch(partialTicks), 0, 0, true));
//			matrixStackIn.rotate(new Quaternion(0,0,180-animatable.getYaw(partialTicks),true));
		}
		
		this.renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		
		this.renderLate(model, animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		
		if (vertexBuilder == null) {
			vertexBuilder = renderTypeBuffer.getBuffer(type);
		}
		
		for (GeoBone group : model.topLevelBones) {
			this.renderRecursively(group, animatable, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		
		matrixStackIn.pop();
	}
	
	public void renderRecursively(GeoBone bone, T animatable, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		stack.push();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		
		handleTranslations(bone, animatable, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);
		
		if (!bone.isHidden) {
			Iterator var10 = bone.childCubes.iterator();
			
			while (var10.hasNext()) {
				GeoCube cube = (GeoCube) var10.next();
				this.renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}
			
			var10 = bone.childBones.iterator();
			
			while (var10.hasNext()) {
				GeoBone childBone = (GeoBone) var10.next();
				this.renderRecursively(childBone, animatable, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}
		}
		
		stack.pop();
	}
	
	public void renderLateBones(GeoBone bone, T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		stackIn.push();
		
		RenderUtils.translate(bone, stackIn);
		RenderUtils.moveToPivot(bone, stackIn);
		RenderUtils.rotate(bone, stackIn);
		
		handleTranslations(bone, animatable, stackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, 1);
		
		RenderUtils.scale(bone, stackIn);
		
		if (bone.name.startsWith("cape_handle")) {
			Vector3d motion = animatable.getPositionVec().subtract(new Vector3d(animatable.prevPosX, animatable.prevPosY, animatable.prevPosZ));
			Client.capes.replace(animatable, (float) MathHelper.lerp(0.1f, Client.capes.get(animatable), Math.abs((20) * motion.distanceTo(new Vector3d(0, 0, 0)) * 10) + (Math.cos(animatable.ticksExisted / 16f) * 2) + 2f));
			stackIn.rotate(new Quaternion(180 - Client.capes.get(animatable), 0, 0, true));
			RenderType capeType = Client.getRenderTypeCape(Client.renderer, animatable, false, true, false);
			
			if (capeType != null)
				Client.renderer.getEntityModel().renderCape(stackIn, renderTypeBuffer.getBuffer(capeType), packedLightIn, packedOverlayIn);
		} else if (bone.name.startsWith("equipment_handle_r")) {
			ItemStack itemStack = animatable.getHeldItem(Hand.MAIN_HAND);
			Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stackIn, renderTypeBuffer);
		} else if (bone.name.startsWith("equipment_handle_l")) {
			ItemStack itemStack = animatable.getHeldItem(Hand.OFF_HAND);
			Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stackIn, renderTypeBuffer);
		} else if (bone.name.startsWith("equipment_handle_hat")) {
			renderArmor(bone, stackIn, animatable, renderTypeBuffer, packedLightIn, EquipmentSlotType.HEAD, null);
		} else if (bone.name.startsWith("equipment_handle_chest")) {
			renderArmor(bone, stackIn, animatable, renderTypeBuffer, packedLightIn, EquipmentSlotType.CHEST, null);
		} else if (bone.name.startsWith("nametag_handle") && !Minecraft.getInstance().gameSettings.hideGUI) {
			if (bone.name.endsWith("_self")) {
				if (animatable.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
					stackIn.push();
					stackIn.rotate(new Quaternion(0, -180 + Client.currentRotBody, 0, true));
					stackIn.translate(0, -2, 0);
					Client.renderName((AbstractClientPlayerEntity) (animatable), animatable.getDisplayName(), stackIn, renderTypeBuffer, packedLightIn);
					stackIn.pop();
				}
			} else if (bone.name.endsWith("_all")) {
				stackIn.push();
				stackIn.rotate(new Quaternion(0, -180 + Client.currentRotBody, 0, true));
				stackIn.translate(0, -2, 0);
				Client.renderName((AbstractClientPlayerEntity) (animatable), animatable.getDisplayName(), stackIn, renderTypeBuffer, packedLightIn);
				stackIn.pop();
			} else {
				if (!animatable.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
					stackIn.push();
					stackIn.rotate(new Quaternion(0, -180 + Client.currentRotBody, 0, true));
					stackIn.translate(0, -2, 0);
					Client.renderName((AbstractClientPlayerEntity) (animatable), animatable.getDisplayName(), stackIn, renderTypeBuffer, packedLightIn);
					stackIn.pop();
				}
			}
		}
		
		RenderUtils.moveBackFromPivot(bone, stackIn);
		
		for (GeoBone bone1 : bone.childBones)
			renderLateBones(bone1, animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		
		stackIn.pop();
	}
	
	public void handleTranslations(GeoBone bone, T animatable, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		float limbSwing = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.limbSwing - 1, animatable.limbSwing);
		float limbSwingAmount = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.prevLimbSwingAmount, animatable.limbSwingAmount);
		float attackSwing = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.prevSwingProgress, animatable.swingProgress);
		
		Vector3d motion = animatable.getPositionVec().subtract(new Vector3d(animatable.prevPosX, animatable.prevPosY, animatable.prevPosZ));
		float speed = (float) motion.distanceTo(new Vector3d(0, 0, 0)) * 5;
		
		//TODO: move to renderEarly
		float bodyTiltCrouch = -0.5F;
		if (bone.name.startsWith("body")) {
			if (animatable.isCrouching()) {
				stack.translate(0, 0, 0.6f);
				stack.rotate(new Quaternion(bodyTiltCrouch, 0, 0, false));
			}
		} else if (bone.name.startsWith("legs")) {
			if (animatable.isPassenger()) {
				stack.rotate(new Quaternion(90, 0, 0, true));
				stack.translate(0, 0.05f, 0.1f);
			}
			
			if (animatable.isCrouching()) {
				stack.rotate(new Quaternion(-bodyTiltCrouch, 0, 0, false));
//				stack.translate(0, 0f, -0.4f);
			}
		} else if (bone.name.startsWith("left_leg")) {
			float rotZ = 0;
			float rotY = 0;
			float rotX = 0;
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.abs(speed)
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.startsWith("right_leg")) {
			float rotZ = 0;
			float rotY = 0;
			float rotX = 0;
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					-((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.abs(speed)
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.startsWith("head")) {
			if (animatable.isCrouching()) stack.rotate(new Quaternion(-bodyTiltCrouch, 0, 0, false));
			
			stack.rotate(new Quaternion(
					0,
					Client.currentRotBody - animatable.getYaw(Client.partialTicks),
					0,
					true
			));
			
			if (!animatable.isElytraFlying()) {
				stack.rotate(new Quaternion(
						-animatable.rotationPitch,
						0,
						0,
						true
				));
			} else {
				stack.rotate(new Quaternion(
						45,
						0,
						0,
						true
				));
			}
		} else if (bone.name.startsWith("right_arm")) {
			float f = MathHelper.sin(attackSwing * (float) Math.PI);
			float f1 = MathHelper.sin((1.0F - (1.0F - attackSwing) * (1.0F - attackSwing)) * (float) Math.PI);
			float rotZ = 0.0F;
			float rotY = -(0.1F - f * 0.6F);
			float f2 = (animatable.getHeldItem(Hand.MAIN_HAND).isEmpty() ? 0 : 0.1F);
			float rotX = f2;
			rotX += (MathHelper.cos(animatable.ticksExisted * 0.09F) * 0.05F + 0.05F);
			rotZ += (MathHelper.sin(animatable.ticksExisted * 0.067F) * 0.05F);
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.abs(speed)
			);
			
			rotX += f * 1.2F - f1 * 0.4F;
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.startsWith("left_arm")) {
			float rotZ = 0.0F;
			float rotY = 0;
			float f2 = (animatable.getHeldItem(Hand.OFF_HAND).isEmpty() ? 0 : 0.1F);
			float rotX = f2;
			rotX += -(MathHelper.cos(animatable.ticksExisted * 0.09F) * 0.05F + 0.05F);
			rotZ += -(MathHelper.sin(animatable.ticksExisted * 0.067F) * 0.05F);
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					-((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.abs(speed)
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		}
	}
	
	public void renderCube(GeoCube cube, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.getLast().getNormal();
		Matrix4f matrix4f = stack.getLast().getMatrix();
		GeoQuad[] var12 = cube.quads;
		int var13 = var12.length;
		
		for (int var14 = 0; var14 < var13; ++var14) {
			GeoQuad quad = var12[var14];
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);
			
			if (normal.getX() < 0.0F) {
				normal.mul(-1.0F, 1.0F, 1.0F);
			}
			
			if (normal.getY() < 0.0F) {
				normal.mul(1.0F, -1.0F, 1.0F);
			}
			
			if (normal.getZ() < 0.0F) {
				normal.mul(1.0F, 1.0F, -1.0F);
			}
			
			GeoVertex[] var17 = quad.vertices;
			int var18 = var17.length;
			
			for (int var19 = 0; var19 < var18; ++var19) {
				GeoVertex vertex = var17[var19];
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
			}
		}
	}
	
	public void renderArmor(GeoBone bone, MatrixStack stackIn, T animatable, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, EquipmentSlotType slotType, Hand side) {
		int packedOverlayIn = OverlayTexture.NO_OVERLAY;
		ItemStack itemStack = animatable.getItemStackFromSlot(slotType);
		
		if (itemStack.getItem() instanceof ArmorItem) {
			ModelRenderer renderer = new ModelRenderer(64, 32, 0, 0);
			
			if (slotType.equals(EquipmentSlotType.HEAD)) {
				stackIn.rotate(new Quaternion(-bone.getRotationX(), -bone.getRotationY(), -bone.getRotationZ(), false));
				stackIn.scale(1f / 8, -1f / 8, 1f / 8);
				stackIn.scale((float) Math.toDegrees(bone.getRotationX()), (float) Math.toDegrees(bone.getRotationY()), (float) Math.toDegrees(bone.getRotationZ()));
				stackIn.translate(-0.01f, -0.48f, -0.01);
				
				renderer.addBox("helmet", 0, 0, 0, 8, 8, 8, 0, 0, 0);
			} else if (slotType.equals(EquipmentSlotType.CHEST)) {
				stackIn.rotate(new Quaternion(-bone.getRotationX(), -bone.getRotationY(), -bone.getRotationZ(), false));
				stackIn.scale(1f / 8, 1f / 11, 1f / 4);
				stackIn.scale((float) Math.toDegrees(bone.getRotationX()), (float) Math.toDegrees(bone.getRotationY()), (float) Math.toDegrees(bone.getRotationZ()));
				stackIn.translate(-0.01f, -0.48f, -0.01);
				
				renderer.addBox("chestplate", 0, 0, 0, 8, 11, 4, 0, 16, 16);
			}
			
			int color;
			float r = 1;
			float g = 1;
			float b = 1;
			
			if (itemStack.getItem() instanceof DyeableArmorItem) {
				color = ((net.minecraft.item.IDyeableArmorItem) itemStack.getItem()).getColor(itemStack);
				r = (float) (color >> 16 & 255) / 255.0F;
				g = (float) (color >> 8 & 255) / 255.0F;
				b = (float) (color & 255) / 255.0F;
			}
			
			renderer.render(stackIn, renderTypeBuffer.getBuffer(RenderType.getArmorCutoutNoCull(Objects.requireNonNull(getTexture(itemStack, animatable, slotType, null)))), packedLightIn, packedOverlayIn, r, g, b, 1);
			
			if (itemStack.getItem() instanceof DyeableArmorItem)
				renderer.render(stackIn, renderTypeBuffer.getBuffer(RenderType.getArmorCutoutNoCull(Objects.requireNonNull(getTexture(itemStack, animatable, slotType, "overlay")))), packedLightIn, packedOverlayIn, r, g, b, 1);
		}
	}
}
