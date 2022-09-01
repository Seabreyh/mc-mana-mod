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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.event.player.PlayerWishEvent;
import com.seabreyh.mana.event.player.PlayerWishEvent.WishType;
import com.seabreyh.mana.items.SealedWishItem;
import com.seabreyh.mana.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.networking.ManaMessages;
import com.seabreyh.mana.registry.ManaItems;

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
            ManaMod.LOGGER.debug("SERVER got sealed wish packet: " + recvWishType);
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            PlayerWishEvent.serverHandlePlayerWish(player, level, recvWishType);
            success.set(true);
        });

        context.setPacketHandled(true);
        return success.get();
    }

    private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {
        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
