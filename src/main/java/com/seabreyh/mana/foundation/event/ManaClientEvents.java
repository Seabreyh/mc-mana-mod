package com.seabreyh.mana.foundation.event;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.items.block_entity_items.renderers.FallenStarBlockEntityItemRenderer;
import com.seabreyh.mana.content.particles.MagicPloomParticleAmethyst;
import com.seabreyh.mana.content.particles.MagicPloomParticleEmerald;
import com.seabreyh.mana.content.particles.MagicPloomParticleFallingStar;
import com.seabreyh.mana.content.particles.MagicPloomParticleFire;
import com.seabreyh.mana.content.particles.MagicPloomParticleStarCatcher;
import com.seabreyh.mana.content.particles.StarPowerParticle;
import com.seabreyh.mana.content.particles.TwinkleParticle;
import com.seabreyh.mana.foundation.client.gui.hud.CarvedMelonOverlay;
import com.seabreyh.mana.foundation.client.gui.hud.ManaHudOverlay;
import com.seabreyh.mana.foundation.client.gui.screens.ManaMenuTypes;
import com.seabreyh.mana.foundation.client.gui.screens.StarCatcherScreen;
import com.seabreyh.mana.foundation.client.gui.screens.WishViewScreen;
import com.seabreyh.mana.foundation.client.renderers.block_entities.RendererBlockEntityStarCatcher;
import com.seabreyh.mana.foundation.client.renderers.entities.AmethystStaffProjectileRenderer;
import com.seabreyh.mana.foundation.client.renderers.entities.EmeraldStaffProjectileRenderer;
import com.seabreyh.mana.foundation.client.renderers.entities.FallenStarRenderer;
import com.seabreyh.mana.foundation.client.renderers.items.ManaItemStackRenderer;
import com.seabreyh.mana.registries.ManaBlockEntities;
import com.seabreyh.mana.registries.ManaBlocks;
import com.seabreyh.mana.registries.ManaEntities;
import com.seabreyh.mana.registries.ManaParticles;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ManaClientEvents {

        public static void WishScreen(WishViewScreen screen) {
                Minecraft.getInstance().setScreen(screen);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(FMLClientSetupEvent event) {
                EntityRenderers.register(ManaEntities.AMETHYST_ENERGY_BALL.get(), AmethystStaffProjectileRenderer::new);
                EntityRenderers.register(ManaEntities.EMERALD_ENERGY_BALL.get(), EmeraldStaffProjectileRenderer::new);
                EntityRenderers.register(ManaEntities.FALLEN_STAR.get(), FallenStarRenderer::new);
                // EntityRenderers.register(ManaEntities.METEOR.get(), MeteorRenderer::new);
        }

        @SubscribeEvent
        public static void registerBlockRenderers(FMLClientSetupEvent event) {
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.ABSTRACT_BLOCK_ENTITY_AS_ITEM.get(),
                                RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.STAR_CATCHER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.STAR_BOTTLE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.CELESTIAL_TORCH.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.FLOWER_BUTTERCUP.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.POTTED_FLOWER_BUTTERCUP.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.PLANT_LEMONBALM.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ManaBlocks.POTTED_PLANT_LEMONBALM.get(), RenderType.cutout());
                // ItemBlockRenderTypes.setRenderLayer(ManaBlocks.CELESTIAL_WALL_TORCH.get(),
                // RenderType.cutout());
        }

        // FIXME NEEDED???
        @SubscribeEvent
        public void clientTick(TickEvent.ClientTickEvent event) {
                if (event.phase == TickEvent.Phase.START) {
                        ManaItemStackRenderer.incrementTick();
                }
        }

        @SubscribeEvent
        public static void registerBlockEntityRenderers(FMLClientSetupEvent event) {

                BlockEntityRenderers.register(ManaBlockEntities.STAR_CATCHER_BLOCK_ENTITY.get(),
                                RendererBlockEntityStarCatcher::new);

                BlockEntityRenderers.register(ManaBlockEntities.FALLEN_STAR_BLOCK_ENTITY_ITEM.get(),
                                FallenStarBlockEntityItemRenderer::new);
        }

        // No @SubscribeEvent
        public static void registerMenuScreens() {

                MenuScreens.register(ManaMenuTypes.STAR_CATCHER_MENU.get(),
                                StarCatcherScreen::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {

                event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "mana_level", ManaHudOverlay.MANA_STAT_HUD);

                event.registerAbove(VanillaGuiOverlay.HELMET.id(), "mana_carved_melon",
                                CarvedMelonOverlay.CARVED_MELON_OVERLAY);

        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_AMETHYST.get(),
                                MagicPloomParticleAmethyst.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_EMERALD.get(),
                                MagicPloomParticleEmerald.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
                                MagicPloomParticleFallingStar.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_FIRE.get(),
                                MagicPloomParticleFire.Provider::new);

                Minecraft.getInstance().particleEngine.register(
                                ManaParticles.MAGIC_PLOOM_PARTICLE_EMERALD_TARGETING.get(),
                                MagicPloomParticleFire.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE_STAR_CATCHER.get(),
                                MagicPloomParticleStarCatcher.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.TWINKLE_PARTICLE.get(),
                                TwinkleParticle.Provider::new);

                Minecraft.getInstance().particleEngine.register(ManaParticles.STAR_POWER.get(),
                                StarPowerParticle.Provider::new);
        }
}