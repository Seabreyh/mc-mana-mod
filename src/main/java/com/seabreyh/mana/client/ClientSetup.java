package com.seabreyh.mana.client;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.renderers.AmethystEnergyBallRenderer;
import com.seabreyh.mana.client.renderers.FallenStarRenderer;
import com.seabreyh.mana.registry.ManaEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(ManaEntities.AMETHYST_ENERGY_BALL.get(), AmethystEnergyBallRenderer::new);
        EntityRenderers.register(ManaEntities.FALLEN_STAR.get(), FallenStarRenderer::new);
    }
}