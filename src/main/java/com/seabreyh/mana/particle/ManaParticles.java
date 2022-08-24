package com.seabreyh.mana.particle;

import com.seabreyh.mana.ManaMod;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaParticles {
        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
                        .create(ForgeRegistries.PARTICLE_TYPES, ManaMod.MOD_ID);

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE = PARTICLE_TYPES
                        .register("magic_ploom_particle", () -> new SimpleParticleType(true));
}
