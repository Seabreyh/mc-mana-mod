package com.seabreyh.mana.init;

import com.seabreyh.mana.ManaMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Init {
    public static <T extends IForgeRegistryEntry<?>> T setup(final T entry, final String name) {
        return setup(entry, new ResourceLocation(ManaMod.MOD_ID, name));
    }

    public static <T extends IForgeRegistryEntry<?>> T setup(final T entry, final ResourceLocation registryName) {
        entry.setRegistryName(registryName);
        return entry;
    }
}