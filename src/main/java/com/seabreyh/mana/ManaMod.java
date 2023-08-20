package com.seabreyh.mana;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.foundation.event.ManaClientEvents;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.proxy.ClientProxy;
import com.seabreyh.mana.foundation.proxy.CommonProxy;
import com.seabreyh.mana.registries.ManaEntityDataSerializers;
// import com.seabreyh.mana.recipes.StaffTableRecipes;
import com.seabreyh.mana.registries.ManaBlockEntities;
import com.seabreyh.mana.registries.ManaBlocks;
// import com.seabreyh.mana.event.ManaClientEvents;
import com.seabreyh.mana.registries.ManaCreativeTab;
import com.seabreyh.mana.registries.ManaEntities;
// import com.seabreyh.mana.gui.ManaMenuTypes;
// import com.seabreyh.mana.networking.ManaMessages;
// import com.seabreyh.mana.registry.ManaBlockEntities;
// import com.seabreyh.mana.registry.ManaBlocks;
// import com.seabreyh.mana.registry.ManaCreativeTab;
// import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.registries.ManaItems;
// import com.seabreyh.mana.registry.ManaPotions;
// import com.seabreyh.mana.registry.ManaRecipes;
// import com.seabreyh.mana.registry.ManaSounds;
import com.seabreyh.mana.registries.ManaParticles;
import com.seabreyh.mana.registries.damage.DamageTypeDataProvider;
import com.seabreyh.mana.registries.damage.DamageTypeTagGen;
import com.seabreyh.mana.content.screens.ManaMenuTypes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
//import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ManaMod.MOD_ID)
public class ManaMod {
    public static final String MOD_ID = "mana";

    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final Logger LOGGER = LogUtils.getLogger();

    public ManaMod() {

        // Register items
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ManaItems.register(eventBus);
        ManaCreativeTab.register(eventBus);
        ManaEntities.register(eventBus);
        ManaParticles.register(eventBus);
        ManaEntityDataSerializers.register(eventBus);
        // ManaSounds.SOUND_EVENTS.register(eventBus);

        ManaBlocks.register(eventBus);
        ManaBlockEntities.register(eventBus);
        ManaMenuTypes.register(eventBus);
        // ManaRecipes.register(eventBus);

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

            // ManaPotions.registerRecipes();

        });

    }

    // @SubscribeEvent
    // UPDATED
    public static void registerRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        // ManaPotions.registerRecipes();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ManaClientEvents.registerEntityRenderers(event);
        ManaClientEvents.registerBlockRenderers(event);
        ManaClientEvents.registerBlockEntityRenderers(event);
        ManaClientEvents.registerOverlays(event);
        ManaClientEvents.registerMenuScreens();
        ManaClientEvents.registerParticleFactories(null);

        PROXY.clientInit();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    // @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        // @SubscribeEvent
        // UPDATED
        public static void onBlocksRegistry(final RegisterEvent.RegisterHelper<Block> blockRegistryEvent) {
            // Register a new block here
            // LOGGER.info("HELLO from Register Block");
        }

        // @SubscribeEvent
        // public static void registerRecipeTypes(final
        // RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        // RegisterEvent.register(Registry.RECIPE_TYPE, StaffTableRecipes.Type.ID,
        // StaffTableRecipes.Type.INSTANCE);
        // }
    }

    public Object getISTERProperties() {
        return null;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void gatherData(GatherDataEvent event) {
        // TagGen.datagen();
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        // if (event.includeClient()) {
        // gen.addProvider(true, AllSoundEvents.provider(gen));
        // LangMerger.attachToRegistrateProvider(gen, output);
        // }

        if (event.includeServer()) {
            // gen.addProvider(true, new AllAdvancements(output));
            // gen.addProvider(true, new StandardRecipeGen(output));
            // gen.addProvider(true, new MechanicalCraftingRecipeGen(output));
            // gen.addProvider(true, new //SequencedAssemblyRecipeGen(output));
            // ProcessingRecipeGen.registerAll(gen, output);
            // gen.addProvider(true,
            // WorldgenDataProvider.makeFactory(event.getLookupProvider()));
            gen.addProvider(true, DamageTypeDataProvider.makeFactory(event.getLookupProvider()));
            gen.addProvider(true,
                    new DamageTypeTagGen(output, event.getLookupProvider(), event.getExistingFileHelper()));
        }
    }
}
