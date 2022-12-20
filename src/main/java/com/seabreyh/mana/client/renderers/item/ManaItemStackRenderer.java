package com.seabreyh.mana.client.renderers.item;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.seabreyh.mana.entity.environment.FallenStar;
import com.seabreyh.mana.registry.ItemTabIcon;
import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.registry.ManaItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManaItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    public ManaItemStackRenderer() {
        super(null, null);
    }

    private static List<Pair<EntityType, Float>> MOB_ICONS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(new Pair<>(ManaEntities.FALLEN_STAR.get(), 0.6F));
    });

    private static float getScaleFor(EntityType type) {
        for (Pair<EntityType, Float> pair : MOB_ICONS) {
            if (pair.getFirst() == type) {
                return pair.getSecond();
            }
        }
        return 1.0F;
    }

    public static int ticksExisted = 0;
    private Map<String, Entity> renderedEntites = new HashMap();

    public static void drawEntityOnScreen(PoseStack matrixstack, int posX, int posY, float scale, boolean follow,
            double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float f = (float) Math.atan(-mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        matrixstack.scale(scale, scale, scale);
        entity.setOnGround(false);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(20.0F);
        float partialTicksForRender = partialTicks;
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }
        if (follow) {
            float yaw = f * 45.0F;
            entity.setYRot(yaw);
            entity.tickCount = tick;
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).yBodyRot = yaw;
                ((LivingEntity) entity).yBodyRotO = yaw;
                ((LivingEntity) entity).yHeadRot = yaw;
                ((LivingEntity) entity).yHeadRotO = yaw;
            }

            quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
        }

        matrixstack.mulPose(quaternion);
        matrixstack.mulPose(Vector3f.XP.rotationDegrees((float) (-xRot)));
        matrixstack.mulPose(Vector3f.YP.rotationDegrees((float) yRot));
        matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float) zRot));
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderdispatcher.overrideCameraOrientation(quaternion1);
        entityrenderdispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers()
                .bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicksForRender, matrixstack,
                    multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        entityrenderdispatcher.setRenderShadow(true);
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).yBodyRot = 0.0F;
            ((LivingEntity) entity).yHeadRotO = 0.0F;
            ((LivingEntity) entity).yHeadRot = 0.0F;
        }
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int tick;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().isPaused()) {
            tick = ticksExisted;
        } else {
            tick = Minecraft.getInstance().player.tickCount;
        }

        if (itemStackIn.getItem() == ManaItems.TAB_ICON.get()) {
            Entity fakeEntity = null;
            int entityIndex = (tick / 40) % (MOB_ICONS.size());
            float scale = 1.0F;
            int flags = 0;
            if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
                flags = itemStackIn.getTag().getInt("DisplayMobFlags");
                String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
                EntityType local = ItemTabIcon.getEntityType(itemStackIn.getTag());
                scale = getScaleFor(local);
                if (itemStackIn.getTag().getFloat("DisplayMobScale") > 0) {
                    scale = itemStackIn.getTag().getFloat("DisplayMobScale");
                }
                if (this.renderedEntites.get(index) == null) {
                    Entity entity = local.create(Minecraft.getInstance().level);

                    this.renderedEntites.put(local.getDescriptionId(), entity);
                    fakeEntity = entity;
                } else {
                    fakeEntity = this.renderedEntites.get(local.getDescriptionId());
                }
            } else {
                EntityType type = MOB_ICONS.get(entityIndex).getFirst();
                scale = MOB_ICONS.get(entityIndex).getSecond();
                if (type != null) {
                    if (this.renderedEntites.get(type.getDescriptionId()) == null) {
                        Entity entity = type.create(Minecraft.getInstance().level);

                        this.renderedEntites.put(type.getDescriptionId(), entity);
                        fakeEntity = entity;
                    } else {
                        fakeEntity = this.renderedEntites.get(type.getDescriptionId());
                    }
                }
            }

            if (fakeEntity instanceof FallenStar) {
                matrixStackIn.translate(0, 0.5F, 0);
            }

        }
    }

}