package com.seabreyh.mana.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.entity.StarCatcherEntityBlock;
import com.seabreyh.mana.entity.FallenStar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class CaughtStarRenderer implements BlockEntityRenderer<StarCatcherEntityBlock>{



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
    public void render(StarCatcherEntityBlock entityBlock, float partialTicks, PoseStack stack,
            MultiBufferSource buffer, int combinedOverlay, int packedLight) {
                float animSpeed = 1.0F;
                stack.pushPose();
                float f = getY(entityBlock, partialTicks);
                float f1 = (partialTicks * animSpeed) * 5.0F;

                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE, true));
                stack.pushPose();
                stack.scale(0.5F, 0.5F, 0.5F);
                stack.translate(1D, 0D, 1D);
                int i = OverlayTexture.NO_OVERLAY;
                stack.mulPose(Vector3f.YP.rotationDegrees(2));
                stack.translate(0.0D, (double) (1.5F + f / 2.0F), 0.0D);

                stack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                this.glass.render(stack, vertexconsumer, 6029544, i);
                stack.scale(0.875F, 0.875F, 0.875F);
                stack.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
                stack.mulPose(Vector3f.YP.rotationDegrees(20));
                stack.scale(0.65F, 0.65F, 0.65F);
                this.cube.render(stack, vertexconsumer, 6029544, i);
                stack.popPose();
                stack.popPose();

              
        
    }
    public static float getY(StarCatcherEntityBlock p_114159_, float partialTicks) {
        float f = (float) partialTicks + 2;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }
    public ResourceLocation getTextureLocation(FallenStar p_114157_) {
        return TEXTURE;
    }
}
