package com.seabreyh.mana.foundation.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.ItemRenderer;

@OnlyIn(Dist.CLIENT)
public class ManaEntityRenderer extends ItemRenderer {
        public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation(
                        "textures/misc/enchanted_item_glint.png");

        public static final int ITEM_COUNT_BLIT_OFFSET = 200;
        public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
        public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
        public float blitOffset;

        public ManaEntityRenderer(Minecraft minecraft, TextureManager textureManager, ModelManager modelManager,
                        ItemColors itemColors, BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer) {
                super(minecraft, textureManager, modelManager, itemColors, blockEntityWithoutLevelRenderer);
        }
}
