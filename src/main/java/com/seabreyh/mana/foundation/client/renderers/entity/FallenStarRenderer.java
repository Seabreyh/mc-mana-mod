package com.seabreyh.mana.foundation.client.renderers.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.FallenStarEntity;
import com.seabreyh.mana.foundation.client.renderers.item.ManaItemStackRenderer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallenStarRenderer<T extends FallenStarEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ManaMod.MOD_ID,
            "textures/entity/fallen_star/fallen_star.png");
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
    private final ModelPart glass;
    private final ModelPart cube;

    public FallenStarRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        this.shadowRadius = 0.0F;
        ModelPart modelpart = manager.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("glass",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("cube",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void render(T fallenStar, float floatOne, float floatTwo, PoseStack poseStack,
            MultiBufferSource multBuff, int intOne) {
        float animSpeed = 3.0F;
        poseStack.pushPose();
        float f = getY(fallenStar, floatTwo);
        float f1 = ((float) fallenStar.tickCount * animSpeed + floatTwo) * 3.0F;
        VertexConsumer vertexconsumer = multBuff.getBuffer(RenderType.entityTranslucent(TEXTURE, true));
        poseStack.pushPose();
        poseStack.scale(0.35F, 0.35F, 0.35F);
        poseStack.translate(0.0D, 0.25D, 0.0D);
        int i = OverlayTexture.NO_OVERLAY;

        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        poseStack.translate(0.0D, (double) (1.5F + f / 1.5F), 0.0D);
        poseStack.mulPose(new Quaternionf(SIN_45, 0.0F, SIN_45, ((float) Math.PI / 6F)));
        poseStack.scale(1.65F, 1.65F, 1.65F);
        this.glass.render(poseStack, vertexconsumer, 6029544, i);

        poseStack.mulPose(new Quaternionf(SIN_45, 0.0F, SIN_45, ((float) Math.PI / 3F)));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(f1));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        this.cube.render(poseStack, vertexconsumer, 6029544, i);

        poseStack.popPose();
        poseStack.popPose();

        super.render(fallenStar, floatOne, floatTwo, poseStack, multBuff, intOne);

        var renderNameTagEvent = new net.minecraftforge.client.event.RenderNameTagEvent(fallenStar,
                fallenStar.getDisplayName(), this, poseStack, multBuff, intOne, floatTwo);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameTagEvent);

        this.renderNameTag(fallenStar, Component.literal(
                "ID:" + fallenStar.getId() +
                        " Age:" + fallenStar.getAge() +
                        " PickUp:" + fallenStar.pickup
                        // " Targeted:" + fallenStar.getIsTargeted() +
                        // " MoveToCatcher: " + fallenStar.getMoveToCatcher() +
                        // " isFalling: " + fallenStar.isFalling +
                        // " moveToCatcher: " + fallenStar.getSyncMoveToCatcher() + " catcher: "
                        // + fallenStar.getCatcher()
                        + " [CLIENT THREAD]"

        ), poseStack, multBuff, intOne);

    }

    // -----------------
    // DEV STAR DATA
    // -----------------

    protected void renderNameTag(T fallenStar, Component text, PoseStack pose,
            MultiBufferSource multBuff,
            int p_114502_) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(fallenStar);
        if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(fallenStar,
                d0)) {
            boolean flag = !fallenStar.isDiscrete();
            float f = fallenStar.getNameTagOffsetY();
            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
            pose.pushPose();
            pose.translate(0.0F, f, 0.0F);
            pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pose.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = pose.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.95F);
            int j = (int) (f1 * 255.0F) << 24;
            Font font = this.getFont();
            float f2 = (float) (-font.width(text) / 2);
            font.drawInBatch(text, f2, (float) i, 553648127, false, matrix4f, multBuff,
                    flag ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH, j, p_114502_);
            if (flag) {
                font.drawInBatch(text, f2, (float) i, -1, false, matrix4f, multBuff,
                        Font.DisplayMode.SEE_THROUGH, 0,
                        p_114502_);
            }

            pose.popPose();
        }
    }

    public static float getY(FallenStarEntity fallenStar, float floatTwo) {
        float f = (float) fallenStar.tickCount + floatTwo;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    public ResourceLocation getTextureLocation(FallenStarEntity fallenStar) {
        return TEXTURE;
    }

    public boolean shouldRender(T fallenStar, Frustum frustum, double doubleOne, double doubleTwo,
            double doubleThree) {
        return super.shouldRender(fallenStar, frustum, doubleOne, doubleTwo, doubleThree);
    }
}
