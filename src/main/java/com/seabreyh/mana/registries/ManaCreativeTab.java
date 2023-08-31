package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class ManaCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ManaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MANA_TAB = CREATIVE_MODE_TABS.register("mana_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ManaItems.FALLEN_STAR_ITEM.get()))
                    .title(Component.translatable("creativetab.mana_tab"))
                    .displayItems((pParameters, pOutPut) -> {

                        // -----------------------
                        // REGISTER TAB ITEMS / BLOCKS
                        // -----------------------

                        pOutPut.accept(ManaBlocks.STAR_CATCHER.get());

                        pOutPut.accept(ManaItems.FALLEN_STAR_ITEM.get());
                        pOutPut.accept(ManaItems.STAR_DUST.get());
                        pOutPut.accept(ManaItems.AMETHYST_STAFF.get());
                        pOutPut.accept(ManaItems.EMERALD_STAFF.get());
                        pOutPut.accept(ManaItems.MANA_CRYSTAL.get());
                        pOutPut.accept(ManaItems.MANA_DUST.get());
                        pOutPut.accept(ManaItems.MANA_SHARD.get());
                        pOutPut.accept(ManaItems.MANA_TREAT.get());
                        pOutPut.accept(ManaItems.EMPTY_MANA_CAPSULE.get());
                        pOutPut.accept(ManaItems.FILLED_MANA_CAPSULE.get());
                        pOutPut.accept(ManaItems.WISH_ITEM.get());
                        pOutPut.accept(ManaItems.SEALED_WISH_ITEM.get());
                        pOutPut.accept(ManaItems.GRANTED_WISH_ITEM.get());

                        pOutPut.accept(ManaBlocks.STAR_BOTTLE.get());
                        pOutPut.accept(ManaBlocks.CELESTIAL_TORCH.get());

                        pOutPut.accept(ManaBlocks.CELESTIAL_BRICKS.get());
                        pOutPut.accept(ManaBlocks.CELESTIAL_BRICK_STAIRS.get());
                        pOutPut.accept(ManaBlocks.CELESTIAL_BRICK_SLAB.get());
                        pOutPut.accept(ManaBlocks.CELESTIAL_BRICK_WALL.get());

                        pOutPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICKS.get());
                        pOutPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_STAIRS.get());
                        pOutPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_SLAB.get());
                        pOutPut.accept(ManaBlocks.MOSSY_CELESTIAL_BRICK_WALL.get());

                        pOutPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICKS.get());
                        pOutPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_STAIRS.get());
                        pOutPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_SLAB.get());
                        pOutPut.accept(ManaBlocks.CRACKED_CELESTIAL_BRICK_WALL.get());

                        pOutPut.accept(ManaBlocks.CINDER_STONE_BRICKS.get());
                        pOutPut.accept(ManaBlocks.CINDER_STONE_BRICK_STAIRS.get());
                        pOutPut.accept(ManaBlocks.CINDER_STONE_BRICK_SLAB.get());
                        pOutPut.accept(ManaBlocks.CINDER_STONE_BRICK_WALL.get());

                        pOutPut.accept(ManaBlocks.COMPACT_BLOCK_OF_AMETHYST.get());
                        pOutPut.accept(ManaBlocks.CARVED_MELON.get());
                        pOutPut.accept(ManaBlocks.FLOWER_BUTTERCUP.get().asItem());
                        pOutPut.accept(ManaBlocks.PLANT_LEMONBALM.get().asItem());

                        // -----------------------
                        // END REGISTER
                        // -----------------------
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
