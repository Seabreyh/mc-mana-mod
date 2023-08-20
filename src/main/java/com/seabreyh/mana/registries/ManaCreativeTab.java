package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.EnchantedBookItem;

import java.lang.reflect.Field;

public class ManaCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ManaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MANA_TAB = CREATIVE_MODE_TABS.register("mana_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ManaItems.STAR_DUST.get()))
                    .title(Component.translatable("creativetab.mana_tab"))
                    .displayItems((pParameters, pOutPut) -> {

                        // -----------------------
                        // REGISTER TAB ITEMS
                        // -----------------------

                        pOutPut.accept(ManaItems.AMETHYST_STAFF.get());
                        pOutPut.accept(ManaItems.EMERALD_STAFF.get());
                        pOutPut.accept(ManaItems.STAR_DUST.get());
                        pOutPut.accept(ManaItems.EMPTY_MANA_CAPSULE.get());
                        pOutPut.accept(ManaItems.FALLEN_STAR_ITEM.get());
                        pOutPut.accept(ManaItems.FILLED_MANA_CAPSULE.get());
                        pOutPut.accept(ManaItems.MANA_CRYSTAL.get());
                        pOutPut.accept(ManaItems.MANA_DUST.get());
                        pOutPut.accept(ManaItems.MANA_SHARD.get());
                        pOutPut.accept(ManaItems.MANA_TREAT.get());
                        pOutPut.accept(ManaItems.WISH_ITEM.get());
                        pOutPut.accept(ManaItems.SEALED_WISH_ITEM.get());
                        pOutPut.accept(ManaItems.GRANTED_WISH_ITEM.get());

                        // -----------------------
                        // REGISTER TAB BLOCKS
                        // -----------------------

                        pOutPut.accept(ManaBlocks.FLOWER_BUTTERCUP.get().asItem());
                        pOutPut.accept(ManaBlocks.PLANT_LEMONBALM.get().asItem());

                        pOutPut.accept(ManaBlocks.STAR_BOTTLE.get());
                        pOutPut.accept(ManaBlocks.CELESTIAL_TORCH.get());
                        pOutPut.accept(ManaBlocks.AMETHYST_BLOCK.get());
                        pOutPut.accept(ManaBlocks.STAR_CATCHER.get());

                        // -----------------------
                        // END REGISTER
                        // -----------------------
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

    // public ManaCreativeTab() {
    // super(ManaMod.MOD_ID);
    // }

    // @Override
    // public ItemStack makeIcon() {
    // // return new ItemStack(ManaItems.TAB_ICON.get());
    // return new ItemStack(ManaItems.AMETHYST_STAFF.get());
    // }

    // @OnlyIn(Dist.CLIENT)
    // public void fillItemList(NonNullList<ItemStack> items) {
    // super.fillItemList(items);
    // try {
    // for (Field f : ManaPotions.class.getDeclaredFields()) {
    // Object obj = f.get(null);
    // if (obj instanceof Potion) {
    // ItemStack potionStack = ManaPotions.createPotion((Potion) obj);
    // items.add(potionStack);
    // }
    // }
    // } catch (IllegalAccessException e) {
    // throw new RuntimeException(e);
    // }
    // try {
    // for (Field f : ManaPotions.class.getDeclaredFields()) {
    // Object obj = f.get(null);
    // if (obj instanceof Enchantment) {
    // Enchantment enchant = (Enchantment) obj;
    // if (enchant.isAllowedOnBooks()) {
    // items.add(EnchantedBookItem
    // .createForEnchantment(new EnchantmentInstance(enchant,
    // enchant.getMaxLevel())));
    // }
    // }
    // }
    // } catch (IllegalAccessException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // // public static final CreativeModeTab MANA_TAB_BLOCKS = new
    // // CreativeModeTab("mana_blocks") {
    // // @Override
    // // public ItemStack makeIcon() {
    // // return new ItemStack(ManaBlocks.STAR_CATCHER.get());
    // // }
    // // };

}
