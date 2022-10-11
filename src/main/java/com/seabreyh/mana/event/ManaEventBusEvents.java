package com.seabreyh.mana.event;

import com.seabreyh.mana.particle.MagicPloomParticleAmethyst;
import com.seabreyh.mana.particle.MagicPloomParticleFallingStar;
import com.seabreyh.mana.particle.MagicPloomParticleFire;
import com.seabreyh.mana.particle.MagicPloomParticleEmerald;
import com.seabreyh.mana.particle.MagicPloomParticleStarCatcher;
import com.seabreyh.mana.particle.StarPowerParticle;
import com.seabreyh.mana.particle.TwinkleParticle;
import com.seabreyh.mana.recipies.StaffTableRecipies;
import com.seabreyh.mana.registry.ManaParticles;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.seabreyh.mana.ManaMod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaEventBusEvents {

        @SubscribeEvent
        public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_AMETHYST.get(),
                                MagicPloomParticleAmethyst.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_EMERALD.get(),
                                MagicPloomParticleEmerald.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
                                MagicPloomParticleFallingStar.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_FIRE.get(),
                                MagicPloomParticleFire.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_STAR_CATCHER.get(),
                                MagicPloomParticleStarCatcher.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.TWINKLE_PARTICLE.get(),
                                TwinkleParticle.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.STAR_POWER.get(),
                                StarPowerParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerRecipeTypes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
                Registry.register(Registry.RECIPE_TYPE, StaffTableRecipies.Type.ID, StaffTableRecipies.Type.INSTANCE);
        }
}
