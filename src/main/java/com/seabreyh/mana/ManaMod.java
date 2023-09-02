package com.seabreyh.mana;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.content.blocks.functional.StarBottle;
import com.seabreyh.mana.foundation.event.ManaClientEvents;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.proxy.ClientProxy;
import com.seabreyh.mana.foundation.proxy.CommonProxy;
import com.seabreyh.mana.foundation.utils.StaffTableRecipe;
import com.seabreyh.mana.registries.ManaEntityDataSerializers;
import com.seabreyh.mana.registries.ManaBlockEntities;
import com.seabreyh.mana.registries.ManaBlocks;
import com.seabreyh.mana.registries.ManaCreativeTab;
import com.seabreyh.mana.registries.ManaEffects;
import com.seabreyh.mana.registries.ManaEntities;
import com.seabreyh.mana.registries.ManaItems;
import com.seabreyh.mana.registries.ManaMenuTypes;
import com.seabreyh.mana.registries.ManaParticles;
import com.seabreyh.mana.registries.ManaPotions;
import com.seabreyh.mana.registries.ManaRecipes;
import com.seabreyh.mana.registries.damage.DamageTypeDataProvider;
import com.seabreyh.mana.registries.damage.DamageTypeTagGen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
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

        ManaPotions.register(eventBus);
        ManaRecipes.register(eventBus);

        ManaEffects.register(eventBus);
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
            ManaBlocks.pottedPlantsSetup(event);
            ManaPotions.registerRecipes();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ManaClientEvents.registerEntityRenderers(event);
        ManaClientEvents.registerBlockRenderers(event);
        ManaClientEvents.registerBlockEntityRenderers(event);
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

    // @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    // public static class RegistryEvents {

    // }

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
