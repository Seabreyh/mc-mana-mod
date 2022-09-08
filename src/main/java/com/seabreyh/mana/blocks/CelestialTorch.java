package com.seabreyh.mana.blocks;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TorchBlock;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CelestialTorch extends Block implements SimpleWaterloggedBlock{
    
   protected final ParticleOptions flameParticle;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final IntegerProperty FLOWING_WATER = IntegerProperty.create("water_level", 1, 8);

    private static final VoxelShape STANDING = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final VoxelShape TORCH_NORTH = Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D);
    private static final VoxelShape TORCH_EAST = Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D);
    private static final VoxelShape TORCH_SOUTH = Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D);
    private static final VoxelShape TORCH_WEST = Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D);

   public CelestialTorch(BlockBehaviour.Properties p_57491_, ParticleOptions p_57492_) {
      super(p_57491_);
      this.flameParticle = p_57492_;
      this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
    return switch (state.getValue(FACING)){
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

   @SuppressWarnings("deprecation")
   @Override
   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }    
    
    // Check the state, if we have an up deal with the break check for standing torches (stole from Torchblock)
        if (stateIn.getValue(FACING) == Direction.UP)
        {
            return facing == Direction.DOWN && !this.canSurvive(stateIn, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
        }
        // Otherwise we do a side check and act accordingly. (stole from WallTorchBlock)
        else
        {
            return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
        }   
}



   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        BlockState blockstate = level.getBlockState(blockpos);

        return blockstate.isFaceSturdy(level, blockpos, direction);
   }

   public void animateTick(BlockState p_57494_, Level p_57495_, BlockPos p_57496_, Random p_57497_) {
      double d0 = (double)p_57496_.getX() + 0.5D;
      double d1 = (double)p_57496_.getY() + 0.7D;
      double d2 = (double)p_57496_.getZ() + 0.5D;
      p_57495_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      p_57495_.addParticle(this.flameParticle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }


   public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        // Set the blockstate to default.
        BlockState blockstate = this.defaultBlockState();
        // Gets world data?
        Level level = context.getLevel();
        // Get the pos we are working with.
        BlockPos blockpos = context.getClickedPos();
        // Return the direction the player was looking.
        Direction[] adirection = context.getNearestLookingDirections();

        // Loop over each direction
        for (Direction direction : adirection)
        {
            // If that direction is a horizontal one.
            if (direction.getAxis().isHorizontal())
            {
                // Flip the direction 180 to set on the wall.
                Direction direction1 = direction.getOpposite();
                // Change the blockstate to match the new direction.
                blockstate = blockstate.setValue(FACING, direction1);
                // If we have a valid spot to place the block, we place it.
                if (blockstate.canSurvive(level, blockpos))
                {
                    return blockstate;
                }
            }
            // If the direction we get back isn't horizontal we place the torch like normal with the default state.
           
            else {
                blockstate = blockstate.setValue(FACING, Direction.UP);

                return blockstate;
            }
        }
        return null;
    }

    

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED, FLOWING_WATER);
    }
    
}

