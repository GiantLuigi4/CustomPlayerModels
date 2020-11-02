package com.tfc.customplayermodels;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.UUID;

public class Server {
	public static HashMap<UUID, String> models = new HashMap<>();
	public static HashMap<Integer, String> anims = new HashMap<>();
	
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof PlayerEntity)
			models.forEach((name, model) -> CustomPlayerModels.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new ModelPacket(model, name, anims.get(name.hashCode()))));
	}
}
