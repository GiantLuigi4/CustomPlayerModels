package com.tfc.customplayermodels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
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
	
	private static final HashMap<ResourceLocation, GeoModel> models = new HashMap<>();
	
	private final GeoModelProvider<T> provider = new GeoModelProvider<T>() {
		@Override
		public ResourceLocation getModelLocation(T t) {
			return new ResourceLocation("cpm:" + t.getName().getUnformattedComponentText());
		}
		
		@Override
		public ResourceLocation getTextureLocation(T t) {
			return ((AbstractClientPlayerEntity) t).getLocationSkin();
		}
		
		@Override
		public GeoModel getModel(ResourceLocation location) {
//			models.clear();
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
	public IResource getResource(ResourceLocation resourceLocationIn) throws IOException {
		return new IResource() {
			ArrayList<InputStream> streams = new ArrayList<>();
			
			@Override
			public ResourceLocation getLocation() {
				return resourceLocationIn;
			}
			
			@Override
			public InputStream getInputStream() {
				InputStream stream = new ByteArrayInputStream(("{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n\t\t\t\t\"identifier\": \"geometry.unknown\",\n\t\t\t\t\"texture_width\": 64,\n\t\t\t\t\"texture_height\": 64,\n\t\t\t\t\"visible_bounds_width\": 4,\n\t\t\t\t\"visible_bounds_height\": 4.5,\n\t\t\t\t\"visible_bounds_offset\": [0, 1.75, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 22, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-8, 15, -8], \"size\": [16, 14, 16], \"uv\": [0, 34]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body2\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 29, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-5, 29, -5], \"size\": [10, 10, 10], \"uv\": [0, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"head\",\n\t\t\t\t\t\"parent\": \"body2\",\n\t\t\t\t\t\"pivot\": [0, 39, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 39, -4], \"size\": [8, 8, 8], \"uv\": [32, 18]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_arm\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [8, 28, 0],\n\t\t\t\t\t\"rotation\": [0, 0, 45],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [8, 27, -1], \"size\": [12, 2, 2], \"uv\": [30, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_arm\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [-8, 28, 0],\n\t\t\t\t\t\"rotation\": [0, 0, -45],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-20, 27, -1], \"size\": [12, 2, 2], \"uv\": [30, 0], \"mirror\": true}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_leg\",\n\t\t\t\t\t\"pivot\": [4, 15, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [2, 0, -2], \"size\": [4, 17, 4], \"uv\": [0, 29]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_leg\",\n\t\t\t\t\t\"pivot\": [-4, 15, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-6, 0, -2], \"size\": [4, 17, 4], \"uv\": [0, 29], \"mirror\": true}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}").getBytes());
//				InputStream stream = new ByteArrayInputStream(("{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n\t\t\t\t\"identifier\": \"geometry.unknown\",\n\t\t\t\t\"texture_width\": 64,\n\t\t\t\t\"texture_height\": 64,\n\t\t\t\t\"visible_bounds_width\": 3,\n\t\t\t\t\"visible_bounds_height\": 4.5,\n\t\t\t\t\"visible_bounds_offset\": [0, 1.75, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"standing\",\n\t\t\t\t\t\"pivot\": [0, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"body\",\n\t\t\t\t\t\"parent\": \"standing\",\n\t\t\t\t\t\"pivot\": [0, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"head\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 24, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"uv\": [0, 0]},\n\t\t\t\t\t\t{\"origin\": [-4, 24, -4], \"size\": [8, 8, 8], \"inflate\": 0.5, \"uv\": [32, 0]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"torso\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 0, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"uv\": [16, 16]},\n\t\t\t\t\t\t{\"origin\": [-4, 12, -2], \"size\": [8, 12, 4], \"inflate\": 0.25, \"uv\": [16, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"arms\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [-3, 22.5, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_arm\",\n\t\t\t\t\t\"parent\": \"arms\",\n\t\t\t\t\t\"pivot\": [-3, 22.5, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"uv\": [40, 16]},\n\t\t\t\t\t\t{\"origin\": [-8, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [40, 32]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_r\",\n\t\t\t\t\t\"parent\": \"right_arm\",\n\t\t\t\t\t\"pivot\": [6, 14, 0],\n\t\t\t\t\t\"rotation\": [22.5, 15, -15]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_arm\",\n\t\t\t\t\t\"parent\": \"arms\",\n\t\t\t\t\t\"pivot\": [3, 22.5, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"uv\": [32, 48]},\n\t\t\t\t\t\t{\"origin\": [4, 12, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [48, 48]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"equipment_handle_l\",\n\t\t\t\t\t\"parent\": \"left_arm\",\n\t\t\t\t\t\"pivot\": [-6, 14, 0],\n\t\t\t\t\t\"rotation\": [22.5, 15, -15]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"legs\",\n\t\t\t\t\t\"parent\": \"body\",\n\t\t\t\t\t\"pivot\": [0, 0, 0]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"right_leg\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [-2, 12.25, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 32]},\n\t\t\t\t\t\t{\"origin\": [-3.9, 0, -2], \"size\": [4, 12, 4], \"uv\": [0, 16]}\n\t\t\t\t\t]\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"left_leg\",\n\t\t\t\t\t\"parent\": \"legs\",\n\t\t\t\t\t\"pivot\": [2, 12.25, 0],\n\t\t\t\t\t\"cubes\": [\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"uv\": [16, 48]},\n\t\t\t\t\t\t{\"origin\": [-0.1, 0, -2], \"size\": [4, 12, 4], \"inflate\": 0.25, \"uv\": [0, 48]}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}").getBytes());
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
	
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (renderTypeBuffer != null) {
			vertexBuilder = renderTypeBuffer.getBuffer(type);
		}
		
		this.renderLate(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		Iterator var14 = model.topLevelBones.iterator();
		
		while (var14.hasNext()) {
			GeoBone group = (GeoBone) var14.next();
			this.renderRecursively(group, animatable, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		
	}
	
	public void renderRecursively(GeoBone bone, T animatable, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		stack.push();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		
		float limbSwing = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.limbSwing - 1, animatable.limbSwing);
		float limbSwingAmount = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.prevLimbSwingAmount, animatable.limbSwingAmount);
		float attackSwing = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), animatable.prevSwingProgress, animatable.swingProgress);
		
		//TODO: move to renderEarly
		float bodyTiltCrouch = -0.5F;
		if (bone.name.equals("body")) {
			if (animatable.isCrouching()) {
				stack.translate(0, 0, 0.6f);
				stack.rotate(new Quaternion(bodyTiltCrouch, 0, 0, false));
			}
		} else if (bone.name.equals("legs")) {
			if (animatable.isCrouching()) {
				stack.rotate(new Quaternion(-bodyTiltCrouch, 0, 0, false));
				stack.translate(0, 0f, -0.4f);
			}
		} else if (bone.name.equals("left_leg")) {
			float rotZ = 0;
			float rotY = 0;
			float rotX = 0;
//			rotX += (MathHelper.cos(animatable.ticksExisted * 0.09F) * 0.05F + 0.05F);
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.max(Math.abs(animatable.moveForward), Math.abs(animatable.moveStrafing))
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.equals("right_leg")) {
			float rotZ = 0;
			float rotY = 0;
			float rotX = 0;
//			rotX += -(MathHelper.cos(animatable.ticksExisted * 0.09F) * 0.05F + 0.05F);
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					-((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.max(Math.abs(animatable.moveForward), Math.abs(animatable.moveStrafing))
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.equals("head")) {
			if (animatable.isCrouching()) stack.rotate(new Quaternion(-bodyTiltCrouch, 0, 0, false));
			
			stack.rotate(new Quaternion(
					0,
					Client.currentRotBody - animatable.getYaw(Client.partialTicks),
					0,
					true
			));
			
			stack.rotate(new Quaternion(
					-animatable.rotationPitch,
					0,
					0,
					true
			));
		} else if (bone.name.equals("right_arm")) {
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
					((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.max(Math.abs(animatable.moveForward), Math.abs(animatable.moveStrafing))
			);
			
			rotX += f * 1.2F - f1 * 0.4F;
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		} else if (bone.name.equals("left_arm")) {
			float rotZ = 0.0F;
			float rotY = 0;
			float f2 = (animatable.getHeldItem(Hand.OFF_HAND).isEmpty() ? 0 : 0.1F);
			float rotX = f2;
			rotX += -(MathHelper.cos(animatable.ticksExisted * 0.09F) * 0.05F + 0.05F);
			rotZ += -(MathHelper.sin(animatable.ticksExisted * 0.067F) * 0.05F);
			
			rotX = MathHelper.lerp(
					limbSwingAmount, rotX,
					-((float) Math.cos(limbSwing / 2f) * limbSwingAmount) * Math.max(Math.abs(animatable.moveForward), Math.abs(animatable.moveStrafing))
			);
			
			stack.rotate(new Quaternion(rotX, rotY, rotZ, false));
		}
		
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
}
