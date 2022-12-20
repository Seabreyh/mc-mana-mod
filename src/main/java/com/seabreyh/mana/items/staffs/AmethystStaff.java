package com.seabreyh.mana.items.staffs;

import com.seabreyh.mana.entity.staff.AmethystEnergyBall;
import com.seabreyh.mana.event.player.PlayerManaEvent;

import java.util.Random;

import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmethystStaff extends Item {

    public AmethystStaff(Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    @Override
    // Called when player right clicks staff
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasMana = false;
        if (!world.isClientSide) {

            if (this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
                // Handle depletion of player mana from use
                hasMana = PlayerManaEvent.consumeMana(player, 1);
                hasMana |= player.isCreative();
            }

            if (hasMana && this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
                AmethystEnergyBall energyBall = new AmethystEnergyBall(world, player);
                energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                energyBall.setNoGravity(true);
                world.addFreshEntity(energyBall);
            }
        }
        if (hasMana && this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {

            this.playSound(world, player);
            this.setDamage(itemstack, this.getDamage(itemstack) + 2);
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

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(new TranslatableComponent("tooltip.mana.amethyst_staff.tooltip"));
        } else {
            pTooltipComponents.add(new TranslatableComponent("tooltip.mana.lshift.tooltip"));
        }
    }
}
