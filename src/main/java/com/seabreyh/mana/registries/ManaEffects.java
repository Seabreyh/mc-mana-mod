package com.seabreyh.mana.registries;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import java.lang.reflect.Field;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.items.brewing.ProperBrewingRecipe;
import com.seabreyh.mana.content.effects.EffectManaRegen;
import com.seabreyh.mana.content.effects.EffectManaInstant;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaEffects {

    public static final DeferredRegister<MobEffect> MANA_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            ManaMod.MOD_ID);

    public static final RegistryObject<MobEffect> MANA_INSTANT = MANA_EFFECTS.register("mana_instant",
            EffectManaInstant::new);

    public static final RegistryObject<MobEffect> MANA_REGEN = MANA_EFFECTS.register("mana_regen",
            EffectManaRegen::new);

    public static void register(IEventBus eventBus) {
        MANA_EFFECTS.register(eventBus);
    }

}
