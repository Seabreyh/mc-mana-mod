package com.seabreyh.mana.networking.packet;

import com.seabreyh.mana.client.gui.WishViewScreen;
import com.seabreyh.mana.event.ManaClientEvents;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenWishS2CPacket {

    public OpenWishS2CPacket() {

    }

    public OpenWishS2CPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // This is the client handling server network event
            WishViewScreen screen = new WishViewScreen();
            ManaClientEvents.WishScreen(screen);
        });
        return true;
    }
}
