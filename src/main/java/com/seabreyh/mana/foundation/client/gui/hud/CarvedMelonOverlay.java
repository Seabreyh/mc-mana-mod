
package com.seabreyh.mana.foundation.client.gui.hud;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.registries.ManaBlocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CarvedMelonOverlay implements IGuiOverlay {

    public static final CarvedMelonOverlay CARVED_MELON_OVERLAY = new CarvedMelonOverlay();
    private static final ResourceLocation MELONBLUR = new ResourceLocation(ManaMod.MOD_ID,
            "textures/misc/melonblur.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft instance = Minecraft.getInstance();
        LocalPlayer player = instance.player;
        screenWidth = instance.getWindow().getGuiScaledWidth();
        screenHeight = instance.getWindow().getGuiScaledHeight();

        if (player != null) {
            ItemStack itemstack = player.getInventory().getArmor(3);

            if (instance.options.getCameraType().isFirstPerson() && !itemstack.isEmpty()) {
                Item item = itemstack.getItem();
                if (item == ManaBlocks.CARVED_MELON.get().asItem()) {
                    renderTextureOverlay(guiGraphics, MELONBLUR, 1.0F, screenWidth, screenHeight);
                } else {
                    IClientItemExtensions.of(item).renderHelmetOverlay(itemstack, instance.player, screenWidth,
                            screenHeight, partialTick);
                }
            }
        }
    }

    protected void renderTextureOverlay(GuiGraphics p_282304_, ResourceLocation p_281622_, float p_281504_,
            int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        p_282304_.setColor(1.0F, 1.0F, 1.0F, p_281504_);
        p_282304_.blit(p_281622_, 0, 0, -90, 0.0F, 0.0F, screenWidth,
                screenHeight, screenWidth,
                screenHeight);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        p_282304_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
