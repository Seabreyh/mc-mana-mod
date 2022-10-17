package com.seabreyh.mana.client.renderers.block_entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.entity.StarCatcherBlockEntity;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaughtStarRenderer implements BlockEntityRenderer<StarCatcherBlockEntity>{

    private static final ResourceLocation TEXTURE = new ResourceLocation(ManaMod.MOD_ID,
            "textures/entity/fallen_star/fallen_star.png");
    private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
    private final ModelPart glass;
    private final ModelPart cube;
    private final BlockEntityRendererProvider.Context context;

    public CaughtStarRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
        this.context = context;
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
    @Override
    public void render(StarCatcherBlockEntity entityBlock, float partialTicks, PoseStack stack,
            MultiBufferSource buffer, int combinedOverlay, int packedLight) {

                stack.pushPose();
                float f1 = entityBlock.getActiveRotation(partialTicks) * (180F / (float)Math.PI);
                
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE, true));
                stack.pushPose();
                stack.scale(0.5F, 0.5F, 0.5F);
                stack.translate(1D, 0D, 1D);

                int i = OverlayTexture.NO_OVERLAY;
                
                stack.mulPose(Vector3f.YP.rotationDegrees(f1));

                stack.translate(0D, 0.93D, 0D);

                stack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 55.0F, true));
                this.glass.render(stack, vertexconsumer, 6029544, i);
                
                stack.scale(0.875F, 0.875F, 0.875F);
                stack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                stack.mulPose(Vector3f.YP.rotationDegrees(f1));
                stack.scale(0.6F, 0.6F, 0.6F);

                this.cube.render(stack, vertexconsumer, 6029544, i);

                stack.popPose();
                stack.popPose();
    }
    
    public ResourceLocation getTextureLocation(FallenStar p_114157_) {
        return TEXTURE;
    }
}
