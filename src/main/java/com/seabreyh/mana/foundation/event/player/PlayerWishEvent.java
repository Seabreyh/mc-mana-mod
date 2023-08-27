package com.seabreyh.mana.foundation.event.player;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.items.SealedWishItem;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.networking.packet.ChoseWishC2SPacket;
import com.seabreyh.mana.registries.ManaItems;

import java.util.Random;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerWishEvent {

    public enum WishType {
        WEATHER_CLEAR, WEATHER_STORM, SUMMON_STAR_FRIEND
    }

    public static WishType fromIndex(int i) {
        switch (i) {
            case 0:
                return WishType.WEATHER_CLEAR;
            case 1:
                return WishType.WEATHER_STORM;
            case 2:
                return WishType.SUMMON_STAR_FRIEND;
        }
        return null;
    }

    public static String displayName(WishType wishType) {
        switch (wishType) {
            case WEATHER_CLEAR:
                return "Wish for clearer weather";
            case WEATHER_STORM:
                return "Wish for stormy weather";
            case SUMMON_STAR_FRIEND:
                return "Wish for a friend";
        }
        return "Unknown";
    }

    public static void makeWishFromIndx(int i) {
        WishType wishType = fromIndex(i);
        makeWish(wishType);

        ManaMessages.sendToServer(new ChoseWishC2SPacket(wishType));
    }

    public static void makeWish(WishType wishType) {
    }

    public static void serverHandlePlayerWish(ServerPlayer player, Level level, WishType recvWishType) {
        ItemStack itemStack = player.getHandSlots().iterator().next();
        if (itemStack != null) {
            int slot = player.getInventory().findSlotMatchingItem(itemStack);
            ItemStack newItemstack = new ItemStack(ManaItems.SEALED_WISH_ITEM.get());
            SealedWishItem.addWishType(newItemstack, recvWishType);
            player.getInventory().setItem(slot, newItemstack);
        }
    }

    public static void starGrantPlayerWish(Player player, Level level) {
        for (ItemStack itemstack : player.getInventory().items) {
            if (itemstack.is(ManaItems.SEALED_WISH_ITEM.get())) {
                int slot = player.getInventory().findSlotMatchingItem(itemstack);
                if (slot >= 0) {
                    ItemStack newItemstack = new ItemStack(ManaItems.GRANTED_WISH_ITEM.get());
                    SealedWishItem.addWishType(newItemstack, SealedWishItem.getWishType(itemstack));
                    player.getInventory().setItem(slot, newItemstack);
                    playWishGrantedSound(level, player);

                }
            }
        }
    }

    private static void playWishGrantedSound(Level level, Player player) {
        RandomSource random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_AMBIENT,
                SoundSource.BLOCKS, 10.25F,
                (random.nextFloat() - random.nextFloat()) * 0.1F + 2.5F);
    }
}
