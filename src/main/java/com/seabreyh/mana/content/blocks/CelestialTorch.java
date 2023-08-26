package com.seabreyh.mana.content.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.ibm.icu.text.MessagePattern.Part;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.registries.ManaParticles;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CelestialTorch extends TorchBlock implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty FLOWING_WATER = IntegerProperty.create("water_level", 1, 8);

    private static final VoxelShape STANDING = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final VoxelShape TORCH_NORTH = Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D);
    private static final VoxelShape TORCH_EAST = Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D);
    private static final VoxelShape TORCH_SOUTH = Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D);
    private static final VoxelShape TORCH_WEST = Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D);

    public CelestialTorch() {
        this(Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .noCollission()
                .sound(SoundType.WOOD)
                .lightLevel(state -> 15)
                .instabreak(),
                ParticleTypes.FLAME);
    }

    public CelestialTorch(Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
        return switch (state.getValue(FACING)) {
            case EAST -> TORCH_EAST;
            case WEST -> TORCH_WEST;
            case SOUTH -> TORCH_SOUTH;
            case NORTH -> TORCH_NORTH;
            default -> STANDING;
        };
    }

    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED) && blockState.getValue(FLOWING_WATER) == 8) {
            return Fluids.WATER.getSource(false);
        } else if (blockState.getValue(WATERLOGGED) && blockState.getValue(FLOWING_WATER) != 8) {
            return Fluids.WATER.getFlowing(blockState.getValue(FLOWING_WATER), false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level,
            BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        // standing torch
        if (stateIn.getValue(FACING) == Direction.UP) {
            return facing == Direction.DOWN && !this.canSurvive(stateIn, level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : stateIn;
        }
        // wall torch
        else {
            return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : stateIn;
        }
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = level.getBlockState(blockpos);

        if (direction == Direction.UP || direction == Direction.DOWN) {
            // allows placement on things such as fences and walls.
            return canSupportCenter(level, pos.below(), Direction.UP);
        } else {
            return blockstate.isFaceSturdy(level, blockpos, direction);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        Direction facing = state.getValue(FACING);
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.6D;
        double z = (double) pos.getZ() + 0.5D;
        for (int i = 0; i < 20; i++) {
            if (rand.nextInt(10) == 0) {
                double d0 = (double) pos.getX() + 0.5D;
                double d1 = (double) pos.getY() + 0.7D;
                double d2 = (double) pos.getZ() + 0.5D;

                level.addParticle(ManaParticles.STAR_POWER.get(),
                        x + 0.3D * (double) facing.getOpposite().getStepX(),
                        y + 0.09D * (double) facing.getOpposite().getStepY(),
                        z + 0.3D * (double) facing.getOpposite().getStepZ(),
                        Math.sin(i * rand.nextInt(10)) * 0.07d, Math.cos(i * rand.nextInt(10)) * 0.07d,
                        Math.sin(i * rand.nextInt(10)) * 0.07d);
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] adirection = context.getNearestLookingDirections();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER || fluidstate.getType() == Fluids.FLOWING_WATER;
        boolean is_flowing = fluidstate.getType() == Fluids.FLOWING_WATER;

        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (blockstate.canSurvive(level, blockpos)) {
                    return blockstate.setValue(WATERLOGGED, flag)
                            .setValue(FLOWING_WATER, is_flowing ? fluidstate.getAmount() : 8);
                }
            } else {
                blockstate = blockstate.setValue(FACING, Direction.UP);

                return blockstate.setValue(WATERLOGGED, flag)
                        .setValue(FLOWING_WATER, is_flowing ? fluidstate.getAmount() : 8);
            }
        }
        return null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, FLOWING_WATER);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter p_49817_, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.mana.celestial_torch.tooltip"));
            pTooltipComponents.add(Component.translatable("tooltip.mana.underwater"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.mana.lshift.tooltip"));
        }
    }
}
