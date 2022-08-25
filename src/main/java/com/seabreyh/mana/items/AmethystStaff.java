package com.seabreyh.mana.items;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.entity.AmethystEnergyBall;

import java.util.Random;

import org.slf4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AmethystStaff extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmethystStaff(Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            LOGGER.info("*Use amethyst staff (server)*");
            AmethystEnergyBall energyBall = new AmethystEnergyBall(world, player);
            energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            energyBall.setNoGravity(true);
            world.addFreshEntity(energyBall);
        }
        this.playSound(world, player);
        return InteractionResultHolder.success(itemstack);
    }

    private void playSound(Level level, Player player) {
        Random random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE,
            SoundSource.BLOCKS, 1.0F,
            (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }
}
