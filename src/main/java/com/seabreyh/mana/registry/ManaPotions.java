package com.seabreyh.mana.registry;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.lang.reflect.Field;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.items.brewing.ProperBrewingRecipe;
import com.seabreyh.mana.items.brewing.effects.EffectManaInstant;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaPotions {

    // MOB EFFECTS
    public static final MobEffect MANA_INSTANT = new EffectManaInstant();

    // POTIONS
    public static final Potion MANA_INSTANT_POTION = new Potion(new MobEffectInstance(MANA_INSTANT, 3600))
            .setRegistryName("mana:mana_instant");

    // REGISTER
    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<MobEffect> event) {
        try {
            for (Field f : ManaPotions.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof MobEffect) {
                    event.getRegistry().register((MobEffect) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        try {
            for (Field f : ManaPotions.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Potion) {
                    event.getRegistry().register((Potion) obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(ManaItems.POTION.get()), potion);
    }

    // POTION RECIPES
    public static void registerRecipes() {
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.THICK)),
                Ingredient.of(ManaItems.MANA_DUST.get()), createPotion(MANA_INSTANT_POTION));
    }
}
