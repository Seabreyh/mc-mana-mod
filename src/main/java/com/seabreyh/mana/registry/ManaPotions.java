package com.seabreyh.mana.registry;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
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
import com.seabreyh.mana.items.brewing.ProperBrewingRecipe;
import com.seabreyh.mana.items.brewing.effects.EffectManaInstant;
import com.seabreyh.mana.items.brewing.effects.EffectManaRegen;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaPotions {

    // MOB EFFECTS
    public static final MobEffect MANA_INSTANT = new EffectManaInstant();
    public static final MobEffect MANA_REGEN = new EffectManaRegen();

    // POTIONS
    public static final Potion MANA_INSTANT_POTION = new Potion(new MobEffectInstance(MANA_INSTANT))
            .setRegistryName("mana:mana_instant");
    public static final Potion MANA_REGEN_POTION = new Potion(new MobEffectInstance(MANA_REGEN, 500))
            .setRegistryName("mana:mana_regen");

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
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    // POTION RECIPES
    public static void registerRecipes() {
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
                ManaItems.STAR_DUST.get(), ManaPotions.MANA_INSTANT_POTION));

        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
                ManaItems.MANA_DUST.get(), ManaPotions.MANA_REGEN_POTION));
    }
}
