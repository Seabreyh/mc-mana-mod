package com.seabreyh.mana.foundation.event.player;

import com.seabreyh.mana.foundation.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.networking.packet.ManaStatSyncS2CPacket;
import com.seabreyh.mana.registries.ManaEffects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class PlayerManaEvent {
    private static boolean hasMana;
    private static boolean maxCapacity;

    public static boolean consumeMana(Player player, int mana) {

        player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
            if (mana_stat.getManaValue() > 0) {
                hasMana = true;
            } else {
                hasMana = false;
            }

            mana_stat.subMana(mana);

            // Send packet with new player mana data to all the clients
            ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(mana_stat.getManaValue(), mana_stat.getManaCapacity()),
                    ((ServerPlayer) player));

        });

        return hasMana;
    }

    public static boolean regenMana(Player player, int mana) {
        player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {

            if (mana_stat.isFull()) {
                maxCapacity = true;
            } else {
                maxCapacity = false;
            }

            mana_stat.addMana(mana);

            // Send packet with new player mana data to all the clients
            ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(mana_stat.getManaValue(), mana_stat.getManaCapacity()),
                    ((ServerPlayer) player));
        });

        return maxCapacity;
    }

    public static void increaseManaCapacity(Player player, int mana_increase) {
        player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {

            mana_stat.addManaCapacity(mana_increase);

            // Mana Treat effect
            player.addEffect(new MobEffectInstance(ManaEffects.MANA_REGEN.get(), 200, 10));

            // Send packet with new player mana data to all the clients
            ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(mana_stat.getManaValue(), mana_stat.getManaCapacity()),
                    ((ServerPlayer) player));
        });

    }

    public static boolean isAtMaxManaLevel(Player player) {
        player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {

            if (mana_stat.isAtMaxCapacity()) {
                maxCapacity = true;
            } else {
                maxCapacity = false;
            }
        });
        return maxCapacity;
    }
}
