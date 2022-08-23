package com.seabreyh.mana.init;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.items.AmethystStaff;

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
            () -> new AmethystStaff(new Item.Properties().tab(ModCreativeTab.instance)));

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
