package com.seabreyh.mana;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    public void init() {
    }

    public void clientInit() {
    }

    public Object getISTERProperties() {
        return null;
    }
}
