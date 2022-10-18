package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.NonNullList;

import java.lang.reflect.Field;

public class ManaCreativeTabs extends CreativeModeTab {

    private ManaCreativeTabs() {
        super(ManaMod.MOD_ID);
    }

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

    @Override
    public ItemStack makeIcon() {
        // TODO Auto-generated method stub
        return null;
    }

}
