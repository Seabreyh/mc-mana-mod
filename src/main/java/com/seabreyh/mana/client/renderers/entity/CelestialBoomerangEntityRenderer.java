package com.seabreyh.mana.client.renderers.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.models.CelestialBoomerangModel;
import com.seabreyh.mana.entity.throwable.CelestialBoomerangEntity;
import com.seabreyh.mana.registry.ManaModelLayers;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CelestialBoomerangEntityRenderer extends EntityRenderer<CelestialBoomerangEntity> {

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
            ManaMod.MOD_ID + ":textures/entity/celestial_boomerang.png");
    private final CelestialBoomerangModel<CelestialBoomerangEntity> model;

    public CelestialBoomerangEntityRenderer(EntityRendererProvider.Context manager) {
        super(manager);
        this.model = new CelestialBoomerangModel(manager.bakeLayer(ManaModelLayers.CELESTIAL_BOOMERANG));
    }

    public ResourceLocation getTextureLocation(CelestialBoomerangEntity p_115860_) {
        return TEXTURE_LOCATION;
    }
}