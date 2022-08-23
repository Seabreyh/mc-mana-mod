package com.seabreyh.mana.client.renderers;

import com.seabreyh.mana.projectiles.AmethystEnergyBall;

import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmethystEnergyBallRenderer extends EntityRenderer<AmethystEnergyBall> {

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    private final ShulkerBulletModel<AmethystEnergyBall> model;

    public AmethystEnergyBallRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        this.model = new ShulkerBulletModel<>(manager.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    public ResourceLocation getTextureLocation(AmethystEnergyBall p_115860_) {
        return TEXTURE_LOCATION;
    }
}