package com.seabreyh.mana.foundation.client.renderers.entities;

import com.seabreyh.mana.content.entities.AmethystStaffProjectile;

import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AmethystStaffProjectileRenderer extends EntityRenderer<AmethystStaffProjectile> {

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private final ShulkerBulletModel<AmethystStaffProjectile> model;

    public AmethystStaffProjectileRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        this.model = new ShulkerBulletModel<>(manager.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    public ResourceLocation getTextureLocation(AmethystStaffProjectile p_115860_) {
        return TEXTURE_LOCATION;
    }
}