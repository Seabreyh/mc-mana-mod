package com.seabreyh.mana.content.blocks.invalid_blocks;

import com.seabreyh.mana.content.items.block_entity_items.FallenStarBlockEntityItem;
import com.seabreyh.mana.registries.ManaBlockEntities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AbstractBlockEntityAsItem extends BaseEntityBlock {

    public static final Properties PROPERTIES = Properties.of()
            .replaceable()
            .noCollission()
            .noLootTable()
            .air();

    public AbstractBlockEntityAsItem(Properties properties) {
        super(properties);
    }

    // Block entity
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FallenStarBlockEntityItem(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ManaBlockEntities.FALLEN_STAR_BLOCK_ENTITY_ITEM.get(),
                FallenStarBlockEntityItem::tick);
    }
}