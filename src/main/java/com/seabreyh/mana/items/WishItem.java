package com.seabreyh.mana.items;

import java.util.Random;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.event.ManaClientEvents;
import com.seabreyh.mana.event.player.PlayerManaEvent;
import com.seabreyh.mana.event.player.PlayerWishEvent;
import com.seabreyh.mana.networking.ManaMessages;
import com.seabreyh.mana.networking.packet.OpenWishS2CPacket;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class WishItem extends Item {

    public WishItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            // DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            // com.seabreyh.mana.client.gui.WishViewScreen screen = new
            // com.seabreyh.mana.client.gui.WishViewScreen(
            // new
            // com.seabreyh.mana.client.gui.WishViewScreen.WrittenBookAccess(itemstack));
            // return ManaClientEvents.WishScreen(screen);
            // });
            ManaMessages.sendToPlayer(new OpenWishS2CPacket(), ((ServerPlayer) player));
        }

        return InteractionResultHolder.fail(itemstack);
    }

}
