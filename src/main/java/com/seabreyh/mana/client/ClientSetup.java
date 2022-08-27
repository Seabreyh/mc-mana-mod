package com.seabreyh.mana.client;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.gui.ManaHudOverlay;
import com.seabreyh.mana.client.renderers.AmethystEnergyBallRenderer;
import com.seabreyh.mana.client.renderers.FallenStarRenderer;
import com.seabreyh.mana.registry.ManaEntities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(ManaEntities.AMETHYST_ENERGY_BALL.get(), AmethystEnergyBallRenderer::new);
        EntityRenderers.register(ManaEntities.FALLEN_STAR.get(), FallenStarRenderer::new);

        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.FOOD_LEVEL_ELEMENT, "Mana Level",
                ManaHudOverlay.MANA_STAT_HUD);
    }
}