package com.seabreyh.mana.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.seabreyh.mana.client.ClientManaStatData;
import com.seabreyh.mana.client.gui.WishViewScreen;
import com.seabreyh.mana.event.ManaClientEvents;

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
