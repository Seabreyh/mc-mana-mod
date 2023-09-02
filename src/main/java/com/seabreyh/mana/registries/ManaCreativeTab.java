package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class ManaCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ManaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MANA_TAB = CREATIVE_MODE_TABS.register("mana_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ManaItems.FALLEN_STAR_ITEM.get()))
                    .title(Component.translatable("creativetab.mana_tab"))
                    .displayItems((parameters, outPut) -> {

                        // -----------------------
                        // REGISTER TAB ITEMS / BLOCKS
                        // This list is the order in which the items will appear in the creative tab
                        // -----------------------

                        outPut.accept(ManaBlocks.STAR_CATCHER.get());

                        outPut.accept(ManaItems.FALLEN_STAR_ITEM.get());
                        outPut.accept(ManaItems.STAR_DUST.get());
                        outPut.accept(ManaItems.AMETHYST_STAFF.get());
                        outPut.accept(ManaItems.EMERALD_STAFF.get());
                        outPut.accept(ManaItems.MANA_CRYSTAL.get());
                        outPut.accept(ManaItems.MANA_DUST.get());
                        outPut.accept(ManaItems.MANA_SHARD.get());
                        outPut.accept(ManaItems.MANA_TREAT.get());
                        outPut.accept(ManaItems.EMPTY_MANA_CAPSULE.get());
                        outPut.accept(ManaItems.FILLED_MANA_CAPSULE.get());
                        outPut.accept(ManaItems.WISH_ITEM.get());
                        outPut.accept(ManaItems.SEALED_WISH_ITEM.get());
                        outPut.accept(ManaItems.GRANTED_WISH_ITEM.get());

                        outPut.accept(ManaBlocks.STAR_BOTTLE.get());
                        outPut.accept(ManaBlocks.CELESTIAL_TORCH.get());

                        outPut.accept(ManaBlocks.CELESTIAL_BRICKS.get());
                        outPut.accept(ManaBlocks.CELESTIAL_BRICK_STAIRS.get());
                        outPut.accept(ManaBlocks.CELESTIAL_BRICK_SLAB.get());
                        outPut.accept(ManaBlocks.CELESTIAL_BRICK_WALL.get());

                        outPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICKS.get());
                        outPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_STAIRS.get());
                        outPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_SLAB.get());
                        outPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_WALL.get());

                        outPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICKS.get());
                        outPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_STAIRS.get());
                        outPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_SLAB.get());
                        outPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_WALL.get());

                        outPut.accept(ManaBlocks.CINDER_STONE_BRICKS.get());
                        outPut.accept(ManaBlocks.CINDER_STONE_BRICK_STAIRS.get());
                        outPut.accept(ManaBlocks.CINDER_STONE_BRICK_SLAB.get());
                        outPut.accept(ManaBlocks.CINDER_STONE_BRICK_WALL.get());

                        outPut.accept(ManaBlocks.COMPACT_BLOCK_OF_AMETHYST.get());
                        outPut.accept(ManaBlocks.CARVED_MELON.get());
                        outPut.accept(ManaBlocks.FLOWER_BUTTERCUP.get().asItem());
                        outPut.accept(ManaBlocks.PLANT_LEMONBALM.get().asItem());

                        // -----------------------
                        // REGISTER Potion Items
                        // -----------------------

                        parameters.holders().lookup(Registries.POTION).ifPresent((regLookupPotion) -> {
                            generatePotionEffectTypes(outPut, regLookupPotion, Items.POTION,
                                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        });

                        parameters.holders().lookup(Registries.POTION).ifPresent((regLookupPotion) -> {
                            generatePotionEffectTypes(outPut, regLookupPotion, Items.SPLASH_POTION,
                                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        });

                        parameters.holders().lookup(Registries.POTION).ifPresent((regLookupPotion) -> {
                            generatePotionEffectTypes(outPut, regLookupPotion, Items.LINGERING_POTION,
                                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        });

                        parameters.holders().lookup(Registries.POTION).ifPresent((regLookupPotion) -> {
                            generatePotionEffectTypes(outPut, regLookupPotion, Items.TIPPED_ARROW,
                                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        });

                        // -----------------------
                        // END REGISTER
                        // -----------------------

                    }).build());

    private static void generatePotionEffectTypes(CreativeModeTab.Output outPut, HolderLookup<Potion> regLookupPotion,
            Item item, CreativeModeTab.TabVisibility tabVisibility) {
        regLookupPotion.listElements().filter((potionsFiltered) -> {
            return !potionsFiltered.is(Potions.EMPTY_ID);
        }).map((potionReference) -> {
            // ManaPotions.setPotion will check if it is a ManaMod potion and if so, add it
            // to the ManaMod Creative Tab
            return ManaPotions.setPotion(new ItemStack(item), potionReference.value());
        }).forEach((p_270000_) -> {
            outPut.accept(p_270000_, tabVisibility);
        });
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
