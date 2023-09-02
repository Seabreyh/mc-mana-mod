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
import com.seabreyh.mana.content.effects.EffectManaInstant;
import com.seabreyh.mana.content.effects.EffectManaRegen;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaPotions {

    public static final DeferredRegister<Potion> MANA_POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS,
            ManaMod.MOD_ID);

    public static final RegistryObject<Potion> MANA_INSTANT_POTION = MANA_POTIONS.register("mana_instant",
            () -> new Potion(new MobEffectInstance(ManaEffects.MANA_INSTANT.get())));

    public static final RegistryObject<Potion> MANA_REGEN_POTION = MANA_POTIONS.register("mana_regen",
            () -> new Potion(new MobEffectInstance(ManaEffects.MANA_REGEN.get(), 500)));

    // public static ItemStack createPotion(Potion potion) {
    // return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    // }

    public static void register(IEventBus eventBus) {
        MANA_POTIONS.register(eventBus);
    }

    // // POTION RECIPES
    // public static void registerRecipes() {
    // BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
    // ManaItems.STAR_DUST.get(), ManaPotions.MANA_INSTANT_POTION));

    // BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
    // ManaItems.MANA_DUST.get(), ManaPotions.MANA_REGEN_POTION));
    // }
}
