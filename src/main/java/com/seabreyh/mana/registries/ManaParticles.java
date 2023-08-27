package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaParticles {
        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
                        .create(ForgeRegistries.PARTICLE_TYPES, ManaMod.MOD_ID);

        // ------------------------------------------------------------
        // DONT FORGET TO REGISTER PARTICLE IN ManaClientEvents.java
        // ------------------------------------------------------------

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_AMETHYST = PARTICLE_TYPES
                        .register("magic_ploom_particle_amethyst", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_EMERALD = PARTICLE_TYPES
                        .register("magic_ploom_particle_emerald", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_FALLING_STAR = PARTICLE_TYPES
                        .register("magic_ploom_particle_falling_star", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_FIRE = PARTICLE_TYPES
                        .register("magic_ploom_particle_fire", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_EMERALD_TARGETING = PARTICLE_TYPES
                        .register("magic_ploom_particle_emerald_targeting", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> TWINKLE_PARTICLE = PARTICLE_TYPES
                        .register("twinkle_particle", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> MAGIC_PLOOM_PARTICLE_STAR_CATCHER = PARTICLE_TYPES
                        .register("magic_ploom_particle_star_catcher", () -> new SimpleParticleType(true));

        public static final RegistryObject<SimpleParticleType> STAR_POWER = PARTICLE_TYPES
                        .register("star_power", () -> new SimpleParticleType(true));

        // -----------------------------
        // End of particle registry
        // -----------------------------

        public static void register(IEventBus eventBus) {
                PARTICLE_TYPES.register(eventBus);
        }
}
