package com.seabreyh.mana.items;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.projectiles.AmethystEnergyBall;
import org.slf4j.Logger;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmethystStaff extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AmethystStaff(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            LOGGER.info("*Use amethyst staff (server)*");

            AmethystEnergyBall energyBall = new AmethystEnergyBall(world, player);
            energyBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 1.0F, 1.5F, 1.0F);
            energyBall.setNoGravity(true);
            world.addFreshEntity(energyBall);
        }
        return InteractionResultHolder.success(itemstack);
    }
}
