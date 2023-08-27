package com.seabreyh.mana.foundation.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.seabreyh.mana.ManaMod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StarCatcherScreen extends AbstractContainerScreen<StarCatcherMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ManaMod.MOD_ID,
            "textures/gui/star_catcher_gui.png");

    protected int imageWidth = 176;
    protected int imageHeight = 176;

    public StarCatcherScreen(StarCatcherMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        // this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
        pPoseStack.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int p_97809_, int p_97810_) {

        // WdColor enumeration
        // https://learn.microsoft.com/en-us/office/vba/api/word.wdcolor
        graphics.drawString(this.font, this.title, this.titleLabelX, 6, 4210752, false);

        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 98, 4210752, false);
    }

    @Override
    public void render(GuiGraphics pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

}