package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.botany.CarvedMelon;
import com.seabreyh.mana.content.blocks.botany.flowers.ManaFlower;
import com.seabreyh.mana.content.blocks.decoration.CompactBlockOfAmethyst;
import com.seabreyh.mana.content.blocks.decoration.ManaStoneBlock;
import com.seabreyh.mana.content.blocks.functional.CelestialTorch;
import com.seabreyh.mana.content.blocks.functional.StarBottle;
import com.seabreyh.mana.content.blocks.functional.StarCatcher;

import com.google.common.base.Supplier;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
                        () -> new StarBottle(StarBottle.PROPERTIES));

        public static final RegistryObject<Block> CELESTIAL_TORCH = registerBlock("celestial_torch",
                        () -> new CelestialTorch(CelestialTorch.PROPERTIES, CelestialTorch.PARTICLE));

        public static final RegistryObject<Block> COMPACT_BLOCK_OF_AMETHYST = registerBlock("compact_block_of_amethyst",
                        () -> new CompactBlockOfAmethyst(CompactBlockOfAmethyst.PROPERTIES));

        public static final RegistryObject<Block> STAR_CATCHER = registerBlock("star_catcher",
                        () -> new StarCatcher(StarCatcher.PROPERTIES));

        public static final RegistryObject<Block> CARVED_MELON = registerBlock("carved_melon",
                        () -> new CarvedMelon(CarvedMelon.PROPERTIES));

        public static final RegistryObject<Block> CELESTIAL_BRICKS = registerBlock("celestial_bricks",
                        () -> new ManaStoneBlock(ManaStoneBlock.CELESTIAL_PROPERTIES));

        public static final RegistryObject<Block> MOSSY_CELESTIAL_BRICKS = registerBlock("mossy_celestial_bricks",
                        () -> new ManaStoneBlock(ManaStoneBlock.CELESTIAL_PROPERTIES));

        public static final RegistryObject<Block> CRACKED_CELESTIAL_BRICKS = registerBlock("cracked_celestial_bricks",
                        () -> new ManaStoneBlock(ManaStoneBlock.CELESTIAL_PROPERTIES));

        public static final RegistryObject<Block> CINDER_STONE_BRICKS = registerBlock("cinder_stone_bricks",
                        () -> new ManaStoneBlock(ManaStoneBlock.PROPERTIES));

        // STAIRS -----------------------

        @SuppressWarnings("deprecation")
        public static final RegistryObject<Block> CELESTIAL_BRICK_STAIRS = registerBlock("celestial_brick_stairs",
                        () -> new StairBlock(CELESTIAL_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(CELESTIAL_BRICKS.get())));

        @SuppressWarnings("deprecation")
        public static final RegistryObject<Block> MOSSY_CELESTIAL_BRICK_STAIRS = registerBlock(
                        "mossy_celestial_brick_stairs",
                        () -> new StairBlock(MOSSY_CELESTIAL_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(MOSSY_CELESTIAL_BRICKS.get())));

        @SuppressWarnings("deprecation")
        public static final RegistryObject<Block> CRACKED_CELESTIAL_BRICK_STAIRS = registerBlock(
                        "cracked_celestial_brick_stairs",
                        () -> new StairBlock(CRACKED_CELESTIAL_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(CRACKED_CELESTIAL_BRICKS.get())));

        @SuppressWarnings("deprecation")
        public static final RegistryObject<Block> CINDER_STONE_BRICK_STAIRS = registerBlock("cinder_stone_brick_stairs",
                        () -> new StairBlock(CINDER_STONE_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(CINDER_STONE_BRICKS.get())));

        // SLABS -----------------------

        public static final RegistryObject<Block> CELESTIAL_BRICK_SLAB = registerBlock("celestial_brick_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> MOSSY_CELESTIAL_BRICK_SLAB = registerBlock(
                        "mossy_celestial_brick_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(MOSSY_CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> CRACKED_CELESTIAL_BRICK_SLAB = registerBlock(
                        "cracked_celestial_brick_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(CRACKED_CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> CINDER_STONE_BRICK_SLAB = registerBlock("cinder_stone_brick_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.copy(CINDER_STONE_BRICKS.get())));

        // WALLS -----------------------

        public static final RegistryObject<Block> CELESTIAL_BRICK_WALL = registerBlock("celestial_brick_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.copy(CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> MOSSY_CELESTIAL_BRICK_WALL = registerBlock(
                        "mossy_celestial_brick_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.copy(MOSSY_CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> CRACKED_CELESTIAL_BRICK_WALL = registerBlock(
                        "cracked_celestial_brick_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.copy(CRACKED_CELESTIAL_BRICKS.get())));

        public static final RegistryObject<Block> CINDER_STONE_BRICK_WALL = registerBlock("cinder_stone_brick_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.copy(CINDER_STONE_BRICKS.get())));

        // public static final RegistryObject<Block> STAFF_TABLE =
        // registerBlock("staff_table",
        // () -> new StaffTable(BlockBehaviour.Properties
        // .of(Material.STONE)
        // .strength(0.2f)
        // .destroyTime(0.3f)
        // .noOcclusion()
        // .lightLevel(BlockState -> 5)));

        // -----------------------
        // REGISTER PLANTS
        // -----------------------

        public static final RegistryObject<Block> FLOWER_BUTTERCUP = registerBlock("flower_buttercup",
                        () -> new FlowerBlock(() -> MobEffects.SATURATION, 7, ManaFlower.FLOWER_PROPERTIES));

        public static final RegistryObject<Block> PLANT_LEMONBALM = registerBlock("plant_lemonbalm",
                        () -> new FlowerBlock(() -> MobEffects.SATURATION, 7, ManaFlower.FLOWER_PROPERTIES));

        // potted plants ----------------

        public static final RegistryObject<Block> POTTED_FLOWER_BUTTERCUP = registerBlockWithoutBlockItem(
                        "potted_flower_buttercup",
                        () -> new FlowerPotBlock(
                                        () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                                        ManaBlocks.FLOWER_BUTTERCUP,
                                        ManaFlower.FLOWER_POT_BLOCK_PROPERTIES));

        public static final RegistryObject<Block> POTTED_PLANT_LEMONBALM = registerBlockWithoutBlockItem(
                        "potted_plant_lemonbalm",
                        () -> new FlowerPotBlock(
                                        () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                                        ManaBlocks.PLANT_LEMONBALM,
                                        ManaFlower.FLOWER_POT_BLOCK_PROPERTIES));

        // Common Setup - Potted plants - Called from Main class ----------

        public static void pottedPlantsSetup(FMLCommonSetupEvent event) {
                ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ManaBlocks.FLOWER_BUTTERCUP.getId(),
                                ManaBlocks.POTTED_FLOWER_BUTTERCUP);

                ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ManaBlocks.PLANT_LEMONBALM.getId(),
                                ManaBlocks.POTTED_PLANT_LEMONBALM);
        }

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
