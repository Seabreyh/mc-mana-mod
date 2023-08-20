package com.seabreyh.mana.foundation.client.renderers.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.BlockEntityStarCatcher;
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
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack matrixStack, MultiBufferSource buffer, int i, int i1) {
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


            matrixStack.mulPose(Axis.YP.rotation(tick + Minecraft.getInstance().getFrameTime()));
           

            mc.getBlockEntityRenderDispatcher().renderItem(new BlockEntityStarCatcher(BlockPos.ZERO, ManaBlocks.STAR_CATCHER.get().defaultBlockState()), matrixStack, buffer, i, i1);
        }
    }
}
