package com.seabreyh.mana.client.renderers.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;

public class ManaItemRenderProperties implements IItemRenderProperties {

    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return new ManaItemStackRenderer();
    }
}
