package com.seabreyh.mana.registry;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.RegistryObject;

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

    public static ItemStack createPotion(RegistryObject<Potion> potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get());
    }

    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static void registerRecipes() {
        // POTION RECIPES
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.THICK)),
                Ingredient.of(ManaItems.MANA_DUST.get()), createPotion(MANA_INSTANT_POTION)));
    }
}
