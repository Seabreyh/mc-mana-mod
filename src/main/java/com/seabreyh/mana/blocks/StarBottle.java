// package com.seabreyh.mana.blocks;

// import java.util.Random;
// import java.util.stream.Stream;

// import com.seabreyh.mana.registry.ManaParticles;

// import net.minecraft.core.BlockPos;
// import net.minecraft.world.level.BlockGetter;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.block.LanternBlock;
// import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.phys.shapes.CollisionContext;
// import net.minecraft.world.phys.shapes.BooleanOp;
// import net.minecraft.world.phys.shapes.Shapes;
// import net.minecraft.world.phys.shapes.VoxelShape;

// public class StarBottle extends LanternBlock {

//     public StarBottle(Properties properties) {
//         super(properties);
//     }

//     private static final VoxelShape SHAPE = Stream.of(
//             Block.box(5, 0, 5, 11, 7, 11),
//             Block.box(6, 7, 6, 10, 8, 10),
//             Block.box(5.5, 8, 5.5, 10.5, 9, 10.5),
//             Block.box(6.5, 7, 6.5, 9.5, 8, 9.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

//     private static final VoxelShape SHAPE_HANGING = Stream.of(
//             Block.box(5, 1, 5, 11, 8, 11),
//             Block.box(6, 8, 6, 10, 9, 10),
//             Block.box(5.5, 9, 5.5, 10.5, 10, 10.5),
//             Block.box(6.5, 8, 6.5, 9.5, 9, 9.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

//     @Override
//     public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//         return pState.getValue(HANGING) ? SHAPE_HANGING : SHAPE;
//     }

//     @Override
//     public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
//         double x = (double) blockPos.getX() + 0.25;
//         double y = (double) blockPos.getY();
//         double z = (double) blockPos.getZ() + 0.25;

//         for (int i = 0; i < 2; i++) {
//             level.addParticle(ManaParticles.STAR_POWER.get(),
//                     x + 0.5 * random.nextDouble(0.2, 0.8),
//                     y + 0.3 * random.nextDouble(0.2, 0.8),
//                     z + 0.5 * random.nextDouble(0.2, 0.8),
//                     Math.sin(i * random.nextDouble(10)) * 0.01d, Math.cos(i * random.nextDouble(10)) * 0.01d,
//                     Math.sin(i * random.nextDouble(10)) * 0.01d);
//         }

//     }

// }

package com.seabreyh.mana.blocks;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

import com.seabreyh.mana.registry.ManaParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

import net.minecraft.world.phys.shapes.VoxelShape;

public class StarBottle extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape AABB = Stream.of(
            Block.box(5, 0, 5, 11, 7, 11),
            Block.box(6, 7, 6, 10, 8, 10),
            Block.box(5.5, 8, 5.5, 10.5, 9, 10.5),
            Block.box(6.5, 7, 6.5, 9.5, 8, 9.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape HANGING_AABB = Stream.of(
            Block.box(5, 1, 5, 11, 8, 11),
            Block.box(6, 8, 6, 10, 9, 10),
            Block.box(5.5, 9, 5.5, 10.5, 10, 10.5),
            Block.box(6.5, 8, 6.5, 9.5, 9, 9.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public StarBottle(BlockBehaviour.Properties p_153465_) {
        super(p_153465_);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_153467_) {
        FluidState fluidstate = p_153467_.getLevel().getFluidState(p_153467_.getClickedPos());

        for (Direction direction : p_153467_.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING,
                        Boolean.valueOf(direction == Direction.UP));
                if (blockstate.canSurvive(p_153467_.getLevel(), p_153467_.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
                }
            }
        }

        return null;
    }

    public VoxelShape getShape(BlockState p_153474_, BlockGetter p_153475_, BlockPos p_153476_,
            CollisionContext p_153477_) {
        return p_153474_.getValue(HANGING) ? HANGING_AABB : AABB;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153490_) {
        p_153490_.add(HANGING, WATERLOGGED);
    }

    public boolean canSurvive(BlockState p_153479_, LevelReader p_153480_, BlockPos p_153481_) {
        Direction direction = getConnectedDirection(p_153479_).getOpposite();
        return Block.canSupportCenter(p_153480_, p_153481_.relative(direction), direction.getOpposite());
    }

    protected static Direction getConnectedDirection(BlockState p_153496_) {
        return p_153496_.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    public BlockState updateShape(BlockState p_153483_, Direction p_153484_, BlockState p_153485_,
            LevelAccessor p_153486_, BlockPos p_153487_, BlockPos p_153488_) {
        if (p_153483_.getValue(WATERLOGGED)) {
            p_153486_.scheduleTick(p_153487_, Fluids.WATER, Fluids.WATER.getTickDelay(p_153486_));
        }

        return getConnectedDirection(p_153483_).getOpposite() == p_153484_
                && !p_153483_.canSurvive(p_153486_, p_153487_) ? Blocks.AIR.defaultBlockState()
                        : super.updateShape(p_153483_, p_153484_, p_153485_, p_153486_, p_153487_, p_153488_);
    }

    public FluidState getFluidState(BlockState p_153492_) {
        return p_153492_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_153492_);
    }

    public boolean isPathfindable(BlockState p_153469_, BlockGetter p_153470_, BlockPos p_153471_,
            PathComputationType p_153472_) {
        return false;
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        double x = (double) blockPos.getX() + 0.25;
        double y = (double) blockPos.getY();
        double z = (double) blockPos.getZ() + 0.25;

        for (int i = 0; i < 2; i++) {
            level.addParticle(ManaParticles.STAR_POWER.get(),
                    x + 0.5 * random.nextDouble(0.2, 0.8),
                    y + 0.3 * random.nextDouble(0.2, 0.8),
                    z + 0.5 * random.nextDouble(0.2, 0.8),
                    Math.sin(i * random.nextDouble(10)) * 0.01d, Math.cos(i * random.nextDouble(10)) * 0.01d,
                    Math.sin(i * random.nextDouble(10)) * 0.01d);
        }
    }
}