package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.botany.flowers.ManaFlower;
import com.seabreyh.mana.content.blocks.decoration.AmethystBlock;
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

        public static final RegistryObject<Block> AMETHYST_BLOCK = registerBlock("amethyst_block",
                        () -> new AmethystBlock(AmethystBlock.PROPERTIES));

        public static final RegistryObject<Block> STAR_CATCHER = registerBlock("star_catcher",
                        () -> new StarCatcher(StarCatcher.PROPERTIES));

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
