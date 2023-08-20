package com.seabreyh.mana.networking.packet;

import com.seabreyh.mana.client.ClientManaStatData;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ManaStatSyncS2CPacket {
    private final int mana_stat;
    private final int mana_capacity;

    public ManaStatSyncS2CPacket(int mana_value, int mana_capacity) {
        this.mana_stat = mana_value;
        this.mana_capacity = mana_capacity;
    }

    public ManaStatSyncS2CPacket(FriendlyByteBuf buf) {
        this.mana_stat = buf.readInt();
        this.mana_capacity = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(mana_stat);
        buf.writeInt(mana_capacity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // This is the client handling server network events for mana stat
            ClientManaStatData.setCurrent(mana_stat);
            ClientManaStatData.setCapacity(mana_capacity);
        });
        return true;
    }
}
