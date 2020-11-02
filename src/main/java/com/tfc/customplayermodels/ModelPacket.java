package com.tfc.customplayermodels;

import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class ModelPacket implements IPacket {
	private String model;
	private UUID uuid;
	private String animation;
	
	public ModelPacket(String model, UUID sender, String animation) {
		this.model = model;
		this.uuid = sender;
		this.animation = animation;
	}
	
	public ModelPacket(PacketBuffer buffer) {
		readPacketData(buffer);
	}
	
	@Override
	public void readPacketData(PacketBuffer buf) {
		model = buf.readString();
		uuid = buf.readUniqueId();
		animation = buf.readString();
		Server.models.put(uuid, model);
		
		if (FMLEnvironment.dist.isClient()) {
			if (AnimatedPlayerGeoRenderer.modelsToLoad.containsKey(new ResourceLocation("cpm", uuid.toString())))
				AnimatedPlayerGeoRenderer.modelsToLoad.replace(new ResourceLocation("cpm", uuid.toString()), model);
			else
				AnimatedPlayerGeoRenderer.modelsToLoad.put(new ResourceLocation("cpm", uuid.toString()), model);
			if (AnimatedPlayerGeoRenderer.modelsToLoad.containsKey(new ResourceLocation("cpm:animation/" + uuid.hashCode() + ".animation.json")))
				AnimatedPlayerGeoRenderer.modelsToLoad.replace(new ResourceLocation("cpm:animation/" + uuid.hashCode() + ".animation.json"), animation);
			else
				AnimatedPlayerGeoRenderer.modelsToLoad.put(new ResourceLocation("cpm:animation/" + uuid.hashCode() + ".animation.json"), animation);
		} else
			CustomPlayerModels.INSTANCE.send(PacketDistributor.ALL.noArg(), new ModelPacket(model, uuid, animation));
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) {
		buf.writeString(model);
		buf.writeUniqueId(uuid);
		buf.writeString(animation);
	}
	
	@Override
	public void processPacket(INetHandler handler) {
	}
}
