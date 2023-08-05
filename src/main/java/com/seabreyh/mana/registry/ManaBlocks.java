package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.AmethystBlock;
import com.seabreyh.mana.blocks.CelestialTorch;
import com.seabreyh.mana.blocks.StarBottle;
import com.seabreyh.mana.blocks.StarCatcher;
import com.seabreyh.mana.blocks.botany.flowers.Flower;
import com.google.common.base.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
                        ManaMod.MOD_ID);

        // -----------------------
        // REGISTER BLOCKS
        // -----------------------

        // INFO: Remember to register your block render type in ManaClientEvents.java if
        // it is a 3D/translucent block.

        // INFO: If you want to make a basic block, you dont need to create a class for
        // that block, just replace new StarCatcher with Block

        public static final RegistryObject<Block> STAR_BOTTLE = registerBlock("star_bottle",
                        () -> new StarBottle());

        public static final RegistryObject<Block> CELESTIAL_TORCH = registerBlock("celestial_torch",
                        () -> new CelestialTorch());

        public static final RegistryObject<Block> AMETHYST_BLOCK = registerBlock("amethyst_block",
                        () -> new AmethystBlock());

        public static final RegistryObject<Block> STAR_CATCHER = registerBlock("star_catcher",
                        () -> new StarCatcher());

        // -----------------------
        // REGISTER FLOWERS
        // Note: Dont forget to register in setup of ManaMod.java
        // -----------------------

        public static final RegistryObject<Block> FLOWER_BUTTERCUP = registerBlock("flower_buttercup",
                        () -> new Flower(BlockBehaviour.Properties
                                        .copy(Blocks.DANDELION)
                                        .noCollission()
                                        .instabreak()
                                        .sound(SoundType.GRASS)));

        public static final RegistryObject<Block> POTTED_FLOWER_BUTTERCUP = registerBlockWithoutBlockItem(
                        "potted_flower_buttercup",
                        () -> new FlowerPotBlock(null, ManaBlocks.FLOWER_BUTTERCUP,
                                        BlockBehaviour.Properties.copy(Blocks.POTTED_DANDELION).noOcclusion()));

        public static final RegistryObject<Block> PLANT_LEMONBALM = registerBlock("plant_lemonbalm",
                        () -> new Flower(BlockBehaviour.Properties
                                        .copy(Blocks.DANDELION)
                                        .noCollission()
                                        .instabreak()
                                        .sound(SoundType.GRASS)));

        public static final RegistryObject<Block> POTTED_PLANT_LEMONBALM = registerBlockWithoutBlockItem(
                        "potted_plant_lemonbalm",
                        () -> new FlowerPotBlock(null, ManaBlocks.PLANT_LEMONBALM,
                                        BlockBehaviour.Properties.copy(Blocks.POTTED_DANDELION).noOcclusion()));

        // -----------------------
        // END REGISTER
        // -----------------------

        private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
                RegistryObject<T> toReturn = BLOCKS.register(name, block);
                registerBlockItem(name, toReturn);
                return toReturn;
        }

        private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
                return ManaItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name,
                        Supplier<T> block) {
                return BLOCKS.register(name, block);
        }

        public static void register(IEventBus eventBus) {
                BLOCKS.register(eventBus);
        }
}
