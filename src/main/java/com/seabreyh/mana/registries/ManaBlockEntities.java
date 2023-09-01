package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.StarCatcherBlockEntity;
import com.seabreyh.mana.content.blocks.botany.CarvedMelon;
import com.seabreyh.mana.content.items.block_entity_items.FallenStarBlockEntityItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaBlockEntities {

        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, ManaMod.MOD_ID);

        public static final RegistryObject<BlockEntityType<StarCatcherBlockEntity>> STAR_CATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
                        .register("star_catcher_block_entity",
                                        () -> BlockEntityType.Builder.of(StarCatcherBlockEntity::new,
                                                        ManaBlocks.STAR_CATCHER.get()).build(null));

        // fallen star item as block entity
        public static final RegistryObject<BlockEntityType<FallenStarBlockEntityItem>> FALLEN_STAR_BLOCK_ENTITY_ITEM = BLOCK_ENTITY_TYPES
                        .register("fallen_star_block_entity_item",
                                        () -> BlockEntityType.Builder.of(FallenStarBlockEntityItem::new,
                                                        ManaBlocks.ABSTRACT_BLOCK_ENTITY_AS_ITEM.get()).build(null));

        // public static final RegistryObject<BlockEntityType<BlockEntityStarCatcher>>
        // FALLEN_STAR_BLOCK_ENTITY_ITEM = BLOCK_ENTITY_TYPES
        // .register("fallen_star_block_entity_item",
        // () -> BlockEntityType.Builder.of(FallenStarBlockEntityItem::new,
        // ManaBlocks.STAR_CATCHER.get()).build(null));

        public static void register(IEventBus eventBus) {
                BLOCK_ENTITY_TYPES.register(eventBus);
        }
}