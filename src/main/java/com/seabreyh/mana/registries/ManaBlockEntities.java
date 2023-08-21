package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.BlockEntityStarCatcher;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, ManaMod.MOD_ID);

        public static final RegistryObject<BlockEntityType<BlockEntityStarCatcher>> STAR_CATCHER_ENTITY_BLOCK = BLOCK_ENTITY_TYPES
                        .register("star_catcher_entity_block",
                                        () -> BlockEntityType.Builder.of(BlockEntityStarCatcher::new,
                                                        ManaBlocks.STAR_CATCHER.get()).build(null));

        public static void register(IEventBus eventBus) {
                BLOCK_ENTITY_TYPES.register(eventBus);
        }
}