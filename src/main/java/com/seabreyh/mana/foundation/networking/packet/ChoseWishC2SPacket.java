package com.seabreyh.mana.foundation.networking.packet;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent.WishType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

public class ChoseWishC2SPacket {
    private static final String MESSAGE_REGEN_MANA = "message.mana.regen_mana";
    private static final String MESSAGE_NO_MANA = "message.mana.no_mana";
    private final WishType wishType;

    public ChoseWishC2SPacket(WishType wishType) {
        this.wishType = wishType;
    }

    public ChoseWishC2SPacket(FriendlyByteBuf buf) {
        this(PlayerWishEvent.fromIndex(buf.readInt()));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(wishType.ordinal());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        final var success = new AtomicBoolean(false);
        context.enqueueWork(() -> {
            WishType recvWishType = this.wishType;
            ServerPlayer player = context.getSender();
            ServerLevel level = (ServerLevel) player.level(); // TODO
            PlayerWishEvent.serverHandlePlayerWish(player, level, recvWishType);
            success.set(true);
        });

        context.setPacketHandled(true);
        return success.get();
    }

    private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level,
            int size) {
        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
