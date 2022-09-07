package com.seabreyh.mana.items.brewing;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.RegistryObject;

public class ManaPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ManaMod.MOD_ID);

    public static final RegistryObject<Potion> MANA_INSTANT = POTIONS.register("mana_instant", 
    () -> new Potion("mana_instant", new MobEffectInstance(ManaMobEffects.MANA_INSTANT.get(), 450, 1)));
    
}
