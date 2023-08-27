package com.seabreyh.mana.foundation.client.renderers.items;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.StarCatcherBlockEntity;
import com.seabreyh.mana.registries.ManaBlockEntities;
import com.seabreyh.mana.registries.ManaBlocks;
import com.seabreyh.mana.registries.ManaItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.joml.Quaternionf;

// @OnlyIn(Dist.CLIENT)
public class ManaItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    StarCatcherBlockEntity catcher = new StarCatcherBlockEntity(BlockPos.ZERO,
            ManaBlocks.STAR_CATCHER.get().defaultBlockState());

    public static int ticksExisted = 0;
    private static final ResourceLocation TEXTURE = new ResourceLocation(ManaMod.MOD_ID,
            "textures/entity/fallen_star/fallen_star.png");
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));

    public ManaItemStackRenderer() {
        super(null, null);

    }

    public static void incrementTick() {
        ticksExisted++;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStack,
            MultiBufferSource buffer, int i, int i1) {
        Minecraft mc = Minecraft.getInstance();
        Item item = stack.getItem();

        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        Level level = Minecraft.getInstance().level;

        if (item == ManaItems.FALLEN_STAR_ITEM.get()) {

            float f1 = (tick * -0.0375F * 6F) * (180F / (float) Math.PI);

            // matrixStack.translate(1.25D * -SIN_45, 1D, 0D * -SIN_45);
            // matrixStack.scale(0.8F, 0.8F, 0.8F);
            // matrixStack.translate(0D, 0.1D, 0D);
            // matrixStack.rotateAround(com.mojang.math.Axis.YP.rotationDegrees(f1), 0.8F,
            // 0F, 0.7F);

            // matrixStack.mulPose(new Quaternionf(SIN_45, 0.0F, SIN_45, ((float) Math.PI /
            // 6F)));
            mc.getBlockEntityRenderDispatcher().renderItem(
                    catcher,
                    matrixStack, buffer, i, i1);
        }
    }
}
