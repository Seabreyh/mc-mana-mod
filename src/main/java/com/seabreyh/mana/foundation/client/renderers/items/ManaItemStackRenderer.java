package com.seabreyh.mana.foundation.client.renderers.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.seabreyh.mana.content.items.block_entity_items.FallenStarBlockEntityItem;
import com.seabreyh.mana.registries.ManaBlocks;
import com.seabreyh.mana.registries.ManaItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// @OnlyIn(Dist.CLIENT)
public class ManaItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    FallenStarBlockEntityItem starItem = new FallenStarBlockEntityItem(BlockPos.ZERO,
            ManaBlocks.ABSTRACT_BLOCK_ENTITY_AS_ITEM.get().defaultBlockState());
    Minecraft mc = Minecraft.getInstance();
    LocalPlayer player = mc.player;
    public static int ticksExisted = 0;
    public static int tick;

    public ManaItemStackRenderer() {
        super(null, null);
    }

    public static void incrementTick() {
        ticksExisted++;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStack,
            MultiBufferSource buffer, int i, int i1) {

        Item item = stack.getItem();

        if (player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = player.tickCount;
        }

        // Fallen Star Item animation renderer
        if (item == ManaItems.FALLEN_STAR_ITEM.get()) {
            float f1 = (tick * -0.0175F * 6F) * (180F / (float) Math.PI); // tick based spin var
            matrixStack.translate(0.5D, 0.5D, 0.5D); // center in ui
            matrixStack.rotateAround(com.mojang.math.Axis.YP.rotationDegrees(f1), 0F, 0F, 0F); // spin
            mc.getBlockEntityRenderDispatcher().renderItem(starItem, matrixStack, buffer, i, i1); // render
        }
    }
}
