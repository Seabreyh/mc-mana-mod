package com.seabreyh.mana.foundation.proxy;

import net.minecraftforge.api.distmarker.OnlyIn;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.client.renderers.items.ManaItemRenderProperties;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {
    public void init() {
    }

    public void clientInit() {
    }

    @Override
    public Object getISTERProperties() {
        return new ManaItemRenderProperties();
    }
}