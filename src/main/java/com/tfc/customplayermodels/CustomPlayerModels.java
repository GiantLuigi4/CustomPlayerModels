package com.tfc.customplayermodels;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("custom_player_models")
public class CustomPlayerModels {
	
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("custom_player_models", "main"),
			() -> "1",
			"1"::equals,
			"1"::equals
	);
	
	public CustomPlayerModels() {
		if (FMLEnvironment.dist.isClient()) {
			MinecraftForge.EVENT_BUS.addListener(Client::onRenderEntity);
			MinecraftForge.EVENT_BUS.addListener(Client::onTick);
		}
		
		MinecraftForge.EVENT_BUS.addListener(Server::onPlayerJoin);
		
		INSTANCE.registerMessage(0, ModelPacket.class, ModelPacket::writePacketData, ModelPacket::new, (packet, contex) -> {
		});
		
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}
}
