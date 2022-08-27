package com.seabreyh.mana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.ClientManaStatData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.gui.IIngameOverlay;

public class ManaHudOverlay {
    private static final ResourceLocation FULL_MANA = new ResourceLocation(ManaMod.MOD_ID,
            "textures/gui/mana_star_full.png");
    private static final ResourceLocation EMPTY_MANA = new ResourceLocation(ManaMod.MOD_ID,
            "textures/gui/mana_star_empty.png");

    public static final IIngameOverlay MANA_STAT_HUD = ((gui, poseStack, partialTick, width, height) -> {

        Minecraft instance = Minecraft.getInstance();
        boolean isMounted = instance.player.getVehicle() instanceof LivingEntity;
        if (isMounted || instance.options.hideGui || !gui.shouldDrawSurvivalElements()) {
            return;
        }

        int x = width / 2;
        int y = height;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EMPTY_MANA);
        for (int i = 0; i < 10; i++) {
            GuiComponent.blit(poseStack, x - 94 + (i * 9), y - 54, 0, 0, 12, 12,
                    12, 12);
        }

        RenderSystem.setShaderTexture(0, FULL_MANA);
        for (int i = 0; i < 10; i++) {
            if (ClientManaStatData.getPlayerManaStat() > i) {
                GuiComponent.blit(poseStack, x - 94 + (i * 9), y - 54, 0, 0, 12, 12,
                        12, 12);
            } else {
                break;
            }
        }

    });
}
