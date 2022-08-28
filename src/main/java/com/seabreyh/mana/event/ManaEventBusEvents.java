package com.seabreyh.mana.event;

import com.seabreyh.mana.particle.MagicPloomParticleDefault;
import com.seabreyh.mana.particle.MagicPloomParticleFire;
import com.seabreyh.mana.particle.ManaParticles;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.seabreyh.mana.ManaMod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaEventBusEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {

        Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_DEFAULT.get(),
                MagicPloomParticleDefault.Provider::new);

        Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_GREEN.get(),
                MagicPloomParticleDefault.Provider::new);

        Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_FIRE.get(),
                MagicPloomParticleFire.Provider::new);

    }
}
