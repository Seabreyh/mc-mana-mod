package com.seabreyh.mana.blocks;

import com.seabreyh.mana.blocks.entity.StarCatcherBlockEntity;
import com.seabreyh.mana.registry.ManaBlockEntities;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StarCatcher extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty FLOWING_WATER = IntegerProperty.create("water_level", 1, 8);

    public StarCatcher(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 1, 2, 14, 2, 14),
            Block.box(4, 1.3, 4, 12, 2.3, 12),
            Block.box(4, 1.3, 4, 12, 13.3, 12),
            Block.box(2, 13, 2, 14, 14, 14),
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(3, 14, 3, 13, 15, 13),
            Block.box(5, 15, 5, 11, 16, 11),
            Block.box(3, 2, 3, 5, 13, 5),
            Block.box(11, 2, 3, 13, 13, 5),
            Block.box(11, 2, 11, 13, 13, 13),
            Block.box(3, 2, 11, 5, 13, 13),
            Block.box(5, 2, 12, 11, 3, 13),
            Block.box(5, 2, 3, 11, 3, 4),
            Block.box(3, 2, 5, 4, 3, 11),
            Block.box(12, 2, 5, 13, 3, 11),
            Block.box(5, 12, 12, 11, 13, 13),
            Block.box(5, 12, 3, 11, 13, 4),
            Block.box(3, 12, 5, 4, 13, 11),
            Block.box(12, 12, 5, 13, 13, 11),
            Block.box(7.5, 2.5, 7.5, 8.5, 3.8, 8.5),
            Block.box(4, 12.7, 4, 12, 13.7, 12),
            Block.box(7.5, 11.2, 7.5, 8.5, 12.5, 8.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    // Waterlogging
    public BlockState updateShape(BlockState thisState, Direction directionToNeighbor, BlockState neighborState,
            LevelAccessor levelAccessor, BlockPos thisPos, BlockPos neighborPos) {
        if (thisState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(thisPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        if (directionToNeighbor == Direction.DOWN && !thisState.canSurvive(levelAccessor, thisPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return thisState;
        }
    }

    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED) && blockState.getValue(FLOWING_WATER) == 8) {
            return Fluids.WATER.getSource(false);
        } else if (blockState.getValue(WATERLOGGED) && blockState.getValue(FLOWING_WATER) != 8) {
            return Fluids.WATER.getFlowing(blockState.getValue(FLOWING_WATER), false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    // Facing
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER || fluidstate.getType() == Fluids.FLOWING_WATER;
        boolean is_flowing = fluidstate.getType() == Fluids.FLOWING_WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag)
                .setValue(FLOWING_WATER, is_flowing ? fluidstate.getAmount() : 8)
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED, FLOWING_WATER);
    }

    // Block entity
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof StarCatcherBlockEntity) {
                ((StarCatcherBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
            Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof StarCatcherBlockEntity) {
                NetworkHooks.openGui(((ServerPlayer) pPlayer), (StarCatcherBlockEntity) entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StarCatcherBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ManaBlockEntities.STAR_CATCHER_ENTITY_BLOCK.get(),
                StarCatcherBlockEntity::tick);
    }

}