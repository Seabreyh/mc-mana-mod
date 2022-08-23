package com.seabreyh.mana.client;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.renderers.AmethystEnergyBallRenderer;
import com.seabreyh.mana.init.ManaEntities;
import com.seabreyh.mana.init.ManaParticles;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ManaEntities.AMETHYST_ENERGY_BALL.get(), AmethystEnergyBallRenderer::new);
    }
}