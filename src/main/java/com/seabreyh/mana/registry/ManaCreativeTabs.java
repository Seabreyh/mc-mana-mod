package com.seabreyh.mana.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ManaCreativeTabs {
    public static final CreativeModeTab MANA_TAB_ITEMS = new CreativeModeTab("mana_items") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ManaItems.AMETHYST_STAFF.get());
        }
    };

    public static final CreativeModeTab MANA_TAB_BLOCKS = new CreativeModeTab("mana_blocks") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ManaBlocks.STAR_CATCHER.get());
        }
    };
    
}
