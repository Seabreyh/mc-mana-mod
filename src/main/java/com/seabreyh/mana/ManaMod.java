package com.seabreyh.mana;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.event.ManaClientEvents;

import com.seabreyh.mana.gui.ManaMenuTypes;
import com.seabreyh.mana.networking.ManaMessages;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaBlocks;
import com.seabreyh.mana.registry.ManaCreativeTab;
import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.registry.ManaParticles;
import com.seabreyh.mana.registry.ManaPotions;
import com.seabreyh.mana.registry.ManaRecipes;
import com.seabreyh.mana.registry.ManaSounds;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ManaMod.MOD_ID)
public class ManaMod {
    public static final String MOD_ID = "mana";
    public static CreativeModeTab TAB = new ManaCreativeTab();
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static final Logger LOGGER = LogUtils.getLogger();

    public ManaMod() {

        // Register items
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ManaItems.ITEMS.register(eventBus);
        ManaEntities.ENTITIES.register(eventBus);
        ManaParticles.PARTICLE_TYPES.register(eventBus);
        ManaSounds.SOUND_EVENTS.register(eventBus);

        ManaBlocks.register(eventBus);
        ManaBlockEntities.register(eventBus);
        ManaMenuTypes.register(eventBus);
        ManaRecipes.register(eventBus);

        // Add listeners
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        PROXY.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        event.enqueueWork(() -> {
            ManaMessages.register();

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ManaBlocks.FLOWER_BUTTERCUP.getId(),
                    ManaBlocks.POTTED_FLOWER_BUTTERCUP);

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ManaBlocks.PLANT_LEMONBALM.getId(),
                    ManaBlocks.POTTED_PLANT_LEMONBALM);

            ManaPotions.registerRecipes();

        });

    }

    @SubscribeEvent
    // UPDATED
    public static void registerRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        ManaPotions.registerRecipes();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ManaClientEvents.registerEntityRenderers(event);
        ManaClientEvents.registerBlockRenderers(event);
        ManaClientEvents.registerBlockEntityRenderers(event);
        ManaClientEvents.registerOverlays(event);
        ManaClientEvents.registerMenuScreens(/* no event pls */);

        PROXY.clientInit();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        // UPDATED
        public static void onBlocksRegistry(final RegisterEvent.RegisterHelper<Block> blockRegistryEvent) {
            // Register a new block here
            // LOGGER.info("HELLO from Register Block");
        }
    }

    public Object getISTERProperties() {
        return null;
    }
}
