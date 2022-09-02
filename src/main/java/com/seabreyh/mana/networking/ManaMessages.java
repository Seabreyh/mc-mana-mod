package com.seabreyh.mana.networking;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.networking.packet.ChoseWishC2SPacket;
import com.seabreyh.mana.networking.packet.ManaStatSyncS2CPacket;
import com.seabreyh.mana.networking.packet.OpenWishS2CPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ManaMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        ManaMod.LOGGER.debug("###REGISTER");
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ManaMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ChoseWishC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ChoseWishC2SPacket::new)
                .encoder(ChoseWishC2SPacket::toBytes)
                .consumer(ChoseWishC2SPacket::handle)
                .add();

        net.messageBuilder(ManaStatSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ManaStatSyncS2CPacket::new)
                .encoder(ManaStatSyncS2CPacket::toBytes)
                .consumer(ManaStatSyncS2CPacket::handle)
                .add();

        net.messageBuilder(OpenWishS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenWishS2CPacket::new)
                .encoder(OpenWishS2CPacket::toBytes)
                .consumer(OpenWishS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
