package com.seabreyh.mana.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.seabreyh.mana.client.ClientManaStatData;

public class ManaStatSyncS2CPacket {
    private final int mana_stat;

    public ManaStatSyncS2CPacket(int mana_value) {
        this.mana_stat = mana_value;
    }

    public ManaStatSyncS2CPacket(FriendlyByteBuf buf) {
        this.mana_stat = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(mana_stat);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // This is the client handling server network events for mana stat
            ClientManaStatData.set(mana_stat);
        });
        return true;
    }
}
