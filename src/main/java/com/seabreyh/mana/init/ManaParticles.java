package com.seabreyh.mana.init;

import com.mojang.serialization.Codec;
import com.seabreyh.mana.ManaMod;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class ManaParticles {
        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
                        .create(ForgeRegistries.PARTICLE_TYPES, ManaMod.MOD_ID);

        public static final RegistryObject<SimpleParticleType> MAGIC_PARTICLES = PARTICLE_TYPES
                        .register("magic_particles", () -> new SimpleParticleType(true));
}
