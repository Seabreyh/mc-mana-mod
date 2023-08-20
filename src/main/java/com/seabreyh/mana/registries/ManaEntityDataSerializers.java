package com.seabreyh.mana.registries;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.client.gui.screens.ManaMenuTypes;
import com.seabreyh.mana.foundation.client.renderers.entity.FallenStarSyncDataSerializer;
import com.seabreyh.mana.foundation.event.ManaClientEvents;
import com.seabreyh.mana.foundation.networking.ManaMessages;
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

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
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

public class ManaEntityDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister
            .create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ManaMod.MOD_ID);

    public static final FallenStarSyncDataSerializer FALLEN_STAR_DATA = new FallenStarSyncDataSerializer();

    public static final RegistryObject<FallenStarSyncDataSerializer> FALLEN_STAR_DATA_ENTRY = REGISTER
            .register("fallen_star_data", () -> FALLEN_STAR_DATA);

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}