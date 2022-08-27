package com.seabreyh.mana;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.client.ClientSetup;
import com.seabreyh.mana.entity.FallenStar;
import com.seabreyh.mana.networking.ManaMessages;
import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.registry.ManaItems;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("mana")
public class ManaMod {
    public static final String MOD_ID = "mana";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ManaMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        // Register the enqueueIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register items
        ManaItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ManaEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ManaParticles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        event.enqueueWork(() -> {
            ManaMessages.register();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.registerEntityRenderers(event);
    }

    // private void enqueueIMC(final InterModEnqueueEvent event) {
    // // Some example code to dispatch IMC to another mod
    // // InterModComms.sendTo("mana", "helloworld", () -> {
    // // LOGGER.info("Hello world from the MDK");
    // // return "Hello world";
    // // });
    // }

    // private void processIMC(final InterModProcessEvent event) {
    // // Some example code to receive and process InterModComms from other mods
    // LOGGER.info("Got IMC {}",
    // event.getIMCStream().map(m ->
    // m.messageSupplier().get()).collect(Collectors.toList()));
    // }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            // LOGGER.info("HELLO from Register Block");
        }
    }

}
