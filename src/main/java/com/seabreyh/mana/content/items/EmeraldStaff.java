package com.seabreyh.mana.content.items;

import com.seabreyh.mana.content.entities.EmeraldStaffProjectile;
import com.seabreyh.mana.foundation.event.player.PlayerManaEvent;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public class EmeraldStaff extends Item {

    public static final Properties PROPERTIES = new Item.Properties().stacksTo(1).durability(500);

    public EmeraldStaff(Properties properties) {
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
                hasMana = PlayerManaEvent.consumeMana(player, 3);
                hasMana |= player.isCreative();
            }
            if (hasMana && this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
                EmeraldStaffProjectile energyBall = new EmeraldStaffProjectile(world, player);
                energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(),
                        0.0F, 1.5F, 1.0F);
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

        RandomSource random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE,
                SoundSource.BLOCKS, 1.0F,
                (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.mana.emerald_staff.tooltip"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.mana.lshift.tooltip"));
        }
    }

}
