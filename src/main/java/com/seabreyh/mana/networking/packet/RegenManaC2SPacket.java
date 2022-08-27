package com.seabreyh.mana.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.networking.ManaMessages;

public class RegenManaC2SPacket {
    private static final String MESSAGE_REGEN_MANA = "message.mana.regen_mana";
    private static final String MESSAGE_NO_MANA = "message.mana.no_mana";

    public RegenManaC2SPacket() {

    }

    public RegenManaC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ManaMod.LOGGER.debug("HERE WE ARE ON THE SERVER");
            // HERE WE ARE ON THE SERVER!
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if (hasWaterAroundThem(player, level, 2)) {
                // Notify the player that water has been drunk
                player.sendMessage(
                        new TranslatableComponent(MESSAGE_REGEN_MANA).withStyle(ChatFormatting.DARK_AQUA),
                        Util.NIL_UUID);
                // play the "gain mana" sound
                level.playSound(null, player.getOnPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                        0.5F, level.random.nextFloat() * 0.1F + 0.9F);

                // increase the water level / thirst level of player
                // Output the current thirst level
                player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                    mana_stat.addMana(1);
                    player.sendMessage(new TextComponent("Current Thirst " + mana_stat.getManaValue())
                            .withStyle(ChatFormatting.AQUA), Util.NIL_UUID);
                    ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(mana_stat.getManaValue()), player);
                });

            } else {
                // Notify the player that there is no water around!
                player.sendMessage(new TranslatableComponent(MESSAGE_NO_MANA).withStyle(ChatFormatting.RED),
                        Util.NIL_UUID);
                // Output the current thirst level
                player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(thirst -> {
                    player.sendMessage(new TextComponent("Current Thirst " + thirst.getManaValue())
                            .withStyle(ChatFormatting.AQUA), Util.NIL_UUID);
                    ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(thirst.getManaValue()), player);
                });
            }
        });
        return true;
    }

    private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {
        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
