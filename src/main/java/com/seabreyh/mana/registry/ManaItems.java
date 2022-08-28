package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.items.AmethystStaff;
import com.seabreyh.mana.items.EmeraldStaff;
import com.seabreyh.mana.items.FallenStarItem;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            ManaMod.MOD_ID);

    public static final RegistryObject<Item> AMETHYST_STAFF = ITEMS.register("amethyst_staff",
            () -> new AmethystStaff(new Item.Properties()
                    .tab(ModCreativeTab.instance)
                    .stacksTo(1)));

    public static final RegistryObject<Item> EMERALD_STAFF = ITEMS.register("emerald_staff",
            () -> new EmeraldStaff(new Item.Properties()
                    .tab(ModCreativeTab.instance)
                    .stacksTo(1)));

    public static final RegistryObject<Item> FALLEN_STAR_ITEM = ITEMS.register("fallen_star_item",
            () -> new FallenStarItem(new Item.Properties()
                    .tab(ModCreativeTab.instance)
                    .stacksTo(64)));

    public static class ModCreativeTab extends CreativeModeTab {
        public static final ModCreativeTab instance = new ModCreativeTab(CreativeModeTab.TABS.length, "mana");

        private ModCreativeTab(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AMETHYST_STAFF.get());
        }
    }
}
