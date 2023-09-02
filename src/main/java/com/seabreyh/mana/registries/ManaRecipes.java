package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.utils.StaffTableRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, ManaMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<StaffTableRecipe>> STAFF_TABLE_SERIALIZER = SERIALIZERS
            .register("staff_table", () -> StaffTableRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }

}
