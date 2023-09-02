package com.seabreyh.mana.registries;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.items.brewing.ProperBrewingRecipe;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaPotions {

    public static final DeferredRegister<Potion> MANA_POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS,
            ManaMod.MOD_ID);

    public static final RegistryObject<Potion> MANA_INSTANT_POTION = MANA_POTIONS.register("mana_instant",
            () -> new Potion(new MobEffectInstance(ManaEffects.MANA_INSTANT.get())));

    public static final RegistryObject<Potion> MANA_REGEN_POTION = MANA_POTIONS.register("mana_regen",
            () -> new Potion(new MobEffectInstance(ManaEffects.MANA_REGEN.get(), 500)));

    // -----------------------
    // END REGISTER POTIONS
    // -----------------------

    public static void register(IEventBus eventBus) {
        MANA_POTIONS.register(eventBus);
    }

    // Used by ManaCreativeTab to add potion items to the creative tab
    public static ItemStack setPotion(ItemStack p_43550_, Potion p_43551_) {
        ResourceLocation resourcelocation = BuiltInRegistries.POTION.getKey(p_43551_);
        // resourceLocation for a Minecraft potion will look like: minecraft:strength
        // for ManaMod potions, it will look like: mana:mana_instant

        // if resourcelocation starts with mana: then we will add the potion item to the
        // Mod Creative Tab
        if (resourcelocation.toString().contains(ManaMod.MOD_ID + ":")) {
            if (p_43551_ == Potions.EMPTY) {
                p_43550_.removeTagKey("Potion");
            } else {
                p_43550_.getOrCreateTag().putString("Potion", resourcelocation.toString());
            }

        }
        return p_43550_;
    }

    // POTION RECIPES
    public static void registerRecipes() {
        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
                ManaItems.STAR_DUST.get(), ManaPotions.MANA_INSTANT_POTION.get()));

        BrewingRecipeRegistry.addRecipe(new ProperBrewingRecipe(Potions.THICK,
                ManaItems.MANA_DUST.get(), ManaPotions.MANA_REGEN_POTION.get()));
    }
}
