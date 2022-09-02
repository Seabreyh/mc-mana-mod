package com.seabreyh.mana.client;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.gui.ManaHudOverlay;
import com.seabreyh.mana.client.renderers.AmethystEnergyBallRenderer;
import com.seabreyh.mana.client.renderers.EmeraldEnergyBallRenderer;
import com.seabreyh.mana.client.renderers.FallenStarRenderer;
import com.seabreyh.mana.registry.ManaBlocks;
import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.screen.ManaMenuTypes;
import com.seabreyh.mana.screen.StarCatcherScreen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(ManaEntities.AMETHYST_ENERGY_BALL.get(), AmethystEnergyBallRenderer::new);
        EntityRenderers.register(ManaEntities.EMERALD_ENERGY_BALL.get(), EmeraldEnergyBallRenderer::new);
        EntityRenderers.register(ManaEntities.FALLEN_STAR.get(), FallenStarRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockRenderers(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ManaBlocks.STAR_CATCHER.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerMenuScreens(FMLClientSetupEvent event) {
        // MenuScreens.register(ManaMenuTypes.STAR_CATCHER_MENU.get(), StarCatcherScreen::new);
    }

    @SubscribeEvent
    public static void registerOverlays(FMLClientSetupEvent event) {
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "Mana Level",
                ManaHudOverlay.MANA_STAT_HUD);
    }
}