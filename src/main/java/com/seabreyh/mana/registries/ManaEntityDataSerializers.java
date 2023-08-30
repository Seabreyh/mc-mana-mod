package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.client.renderers.entities.FallenStarSyncDataSerializer;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

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