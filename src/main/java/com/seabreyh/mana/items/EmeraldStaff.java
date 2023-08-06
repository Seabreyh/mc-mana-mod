package com.seabreyh.mana.items;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.EmeraldEnergyBall;
// import com.seabreyh.mana.event.player.PlayerManaEvent;

import java.util.List;
import java.util.Random;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

public class EmeraldStaff extends Item {

    public static final Properties PROPERTIES = new Item.Properties().stacksTo(1);

    public EmeraldStaff(Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    @Override
    // Called when player right clicks staff
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ManaMod.LOGGER.info("EmeraldStaff.use() called");

        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasMana = true;

        if (!world.isClientSide) {
            ManaMod.LOGGER.info("EmeraldStaff.use() called, not client side");
            if (this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
                // Handle depletion of player mana from use
                // hasMana = PlayerManaEvent.consumeMana(player, 3);
                hasMana |= player.isCreative();

                ManaMod.LOGGER.info("EmeraldStaff.use() called, hasMana = " + hasMana);
            }
            // if (hasMana && this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
            if (hasMana) {
                EmeraldEnergyBall energyBall = new EmeraldEnergyBall(world, player);
                energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(),
                        0.0F, 1.5F, 1.0F);
                energyBall.setNoGravity(true);
                world.addFreshEntity(energyBall);

                ManaMod.LOGGER.info("EmeraldStaff.use() called, hasMana = " + hasMana);
            }
        }
        if (hasMana && this.getDamage(itemstack) < this.getMaxDamage(itemstack)) {
            this.playSound(world, player);
            this.setDamage(itemstack, this.getDamage(itemstack) + 2);

            ManaMod.LOGGER.info("Item Damage: " + this.getDamage(itemstack) + " / " + this.getMaxDamage(itemstack));

            return InteractionResultHolder.success(itemstack);
        } else {

            ManaMod.LOGGER.info("FAILED damage item");

            return InteractionResultHolder.fail(itemstack);
        }
    }

    private void playSound(Level level, Player player) {

        ManaMod.LOGGER.info("EmeraldStaff.playSound() called");

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
