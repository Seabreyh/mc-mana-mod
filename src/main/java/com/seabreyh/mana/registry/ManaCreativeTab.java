package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.EnchantedBookItem;

import java.lang.reflect.Field;

public class ManaCreativeTab extends CreativeModeTab {

    public ManaCreativeTab() {
        super(ManaMod.MOD_ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ManaItems.TAB_ICON.get());
    }

    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {
        super.fillItemList(items);
        try {
            for (Field f : ManaPotions.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Potion) {
                    ItemStack potionStack = ManaPotions.createPotion((Potion) obj);
                    items.add(potionStack);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            for (Field f : ManaPotions.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Enchantment) {
                    Enchantment enchant = (Enchantment) obj;
                    if (enchant.isAllowedOnBooks()) {
                        items.add(EnchantedBookItem
                                .createForEnchantment(new EnchantmentInstance(enchant, enchant.getMaxLevel())));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    // public static final CreativeModeTab MANA_TAB_BLOCKS = new
    // CreativeModeTab("mana_blocks") {
    // @Override
    // public ItemStack makeIcon() {
    // return new ItemStack(ManaBlocks.STAR_CATCHER.get());
    // }
    // };

}
