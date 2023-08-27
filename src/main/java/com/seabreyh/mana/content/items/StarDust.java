package com.seabreyh.mana.content.items;

import net.minecraft.world.level.ClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

public class StarDust extends Item {

    public static final Properties PROPERTIES = new Item.Properties();

    public StarDust(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);

        boolean replaced = false;

        if (!world.isClientSide) {
            if (blockhitresult.getType() == BlockHitResult.Type.BLOCK) {

                BlockPos blockpos = blockhitresult.getBlockPos();
                BlockState blockstate = world.getBlockState(blockpos);

                if (blockstate.getBlock() == Blocks.AMETHYST_BLOCK) {

                    world.setBlockAndUpdate(blockpos, Blocks.BUDDING_AMETHYST.defaultBlockState());

                    for (int i = 0; i < 4; ++i) {
                        world.playSound((Player) null, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
                                SoundEvents.AMETHYST_BLOCK_CHIME,
                                SoundSource.AMBIENT, 20.0F, 1.2F / 0.2F + 0.9F);
                    }

                    world.playSound((Player) null, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
                            SoundEvents.AXE_SCRAPE,
                            SoundSource.AMBIENT, 3.0F, -6.0F / 0.2F + 0.9F);

                    ((ServerLevel) world).sendParticles(ParticleTypes.FLASH, blockpos.getX(), blockpos.getY(),
                            blockpos.getZ(),
                            1,
                            0D,
                            1D, 1D, 1D);
                    ((ServerLevel) world).sendParticles(ParticleTypes.END_ROD, blockpos.getX(), blockpos.getY(),
                            blockpos.getZ(),
                            20,
                            1D, 1D, 1D, 0.3D);

                    itemstack.shrink(1);
                    replaced = true;
                }
            }
        }

        if (replaced) {
            return InteractionResultHolder.success(itemstack);

        } else {
            return InteractionResultHolder.fail(itemstack);

        }

    }

}
