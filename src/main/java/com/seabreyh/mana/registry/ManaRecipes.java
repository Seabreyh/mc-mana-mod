package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.recipes.StaffTableRecipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
                        .create(ForgeRegistries.RECIPE_SERIALIZERS, ManaMod.MOD_ID);

        public static final RegistryObject<RecipeSerializer<StaffTableRecipes>> STAFF_TABLE_SERIALIZER = SERIALIZERS
                        .register("staff_table", () -> StaffTableRecipes.Serializer.INSTANCE);

        public static void register(IEventBus eventBus) {
                SERIALIZERS.register(eventBus);
        }
}