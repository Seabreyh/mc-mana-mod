package com.seabreyh.mana.items;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.AmethystEnergyBall;
import com.seabreyh.mana.event.player.PlayerManaEvent;

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
    // Called when player right clicks staff
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ManaMod.LOGGER.debug("### USE AMETHYST STAFF");

        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasMana = false;
        if (!world.isClientSide) {

            // Handle depletion of player mana from use
            hasMana = PlayerManaEvent.consumeMana(player, 1);
            hasMana |= player.isCreative();
            if (hasMana) {
                LOGGER.info("*Use amethyst staff (server)*");
                AmethystEnergyBall energyBall = new AmethystEnergyBall(world, player);
                energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                energyBall.setNoGravity(true);
                world.addFreshEntity(energyBall);
            }
        }
        if (hasMana) {
            this.playSound(world, player);
            return InteractionResultHolder.success(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    private void playSound(Level level, Player player) {
        Random random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.BLOCKS, 1.0F,
                (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }
}
