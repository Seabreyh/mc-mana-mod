package com.seabreyh.mana.foundation.proxy;

import com.seabreyh.mana.ManaMod;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    public void init() {
    }

    public void clientInit() {
    }

    // Return null for server, for Client refer to ClientProxy
    public Object getISTERProperties() {
        return null;
    }
}
