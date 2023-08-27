package com.seabreyh.mana.foundation.client.renderers.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ManaItemRenderProperties implements IClientItemExtensions {

    // public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
    // return new
    // ManaItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
    // Minecraft.getInstance().getEntityModels());
    // }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new ManaItemStackRenderer();
    }
}
