package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.entity.StaffTableBlockEntity;
import com.seabreyh.mana.blocks.entity.StarCatcherBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ManaMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<StarCatcherBlockEntity>> STAR_CATCHER_ENTITY_BLOCK =
            BLOCK_ENTITIES.register("star_catcher_entity_block", () ->
                    BlockEntityType.Builder.of(StarCatcherBlockEntity::new,
                            ManaBlocks.STAR_CATCHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<StaffTableBlockEntity>> STAFF_TABLE_ENTITY_BLOCK =
            BLOCK_ENTITIES.register("staff_table_entity_block", () ->
                    BlockEntityType.Builder.of(StaffTableBlockEntity::new,
                            ManaBlocks.STAFF_TABLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}