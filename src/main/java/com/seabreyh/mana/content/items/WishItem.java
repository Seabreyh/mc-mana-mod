package com.seabreyh.mana.content.items;

import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.networking.packet.OpenWishS2CPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WishItem extends Item {

    public static final Properties PROPERTIES = new Item.Properties().stacksTo(1);

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
