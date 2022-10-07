package com.seabreyh.mana.client.renderers.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.FallenStar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallenStarRenderer extends EntityRenderer<FallenStar> {
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

    public void render(FallenStar fallenStar, float floatOne, float floatTwo, PoseStack poseStack,
            MultiBufferSource multBuff, int intOne) {
        float animSpeed = 3.0F;
        poseStack.pushPose();
        float f = getY(fallenStar, floatTwo);
        float f1 = ((float) fallenStar.getAge() * animSpeed + floatTwo) * 3.0F;
        VertexConsumer vertexconsumer = multBuff.getBuffer(RenderType.entityTranslucent(TEXTURE, true));
        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.translate(0.0D, -0.25D, 0.0D);
        int i = OverlayTexture.NO_OVERLAY;

        poseStack.mulPose(Vector3f.YP.rotationDegrees(f1));
        poseStack.translate(0.0D, (double) (1.5F + f / 2.0F), 0.0D);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
        this.glass.render(poseStack, vertexconsumer, 6029544, i);

        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f1));
        poseStack.scale(0.65F, 0.65F, 0.65F);
        this.cube.render(poseStack, vertexconsumer, 6029544, i);

        poseStack.popPose();
        poseStack.popPose();

        super.render(fallenStar, floatOne, floatTwo, poseStack, multBuff, intOne);
    }

    public static float getY(FallenStar fallenStar, float floatTwo) {
        float f = (float) fallenStar.getAge() + floatTwo;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    public ResourceLocation getTextureLocation(FallenStar fallenStar) {
        return TEXTURE;
    }

    public boolean shouldRender(FallenStar fallenStar, Frustum frustum, double doubleOne, double doubleTwo,
            double doubleThree) {
        return super.shouldRender(fallenStar, frustum, doubleOne, doubleTwo, doubleThree);
    }
}
