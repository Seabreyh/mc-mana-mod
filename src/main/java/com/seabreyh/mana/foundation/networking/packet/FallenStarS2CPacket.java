package com.seabreyh.mana.foundation.networking.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.BlockEntityStarCatcher;
import com.seabreyh.mana.content.entities.FallenStarEntity;
import com.seabreyh.mana.foundation.client.ClientFallenStarData;
import com.seabreyh.mana.foundation.client.ClientManaStatData;
import com.seabreyh.mana.foundation.client.renderers.entity.FallenStarSyncData;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class FallenStarS2CPacket {
    private BlockPos catcherPos;
    private int entityId;
    private FallenStarEntity fsE;

    public FallenStarS2CPacket(BlockPos catcherPos, int entityId) {
        this.catcherPos = catcherPos;
        this.entityId = entityId;
    }

    public FallenStarS2CPacket(FriendlyByteBuf buf) {
        this.catcherPos = (buf.readBlockPos());
        this.entityId = (buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        // buf.writeInt(star_catcher);
        buf.writeBlockPos(catcherPos);
        buf.writeInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player.position()
                    .distanceTo(new Vec3(catcherPos.getX(), catcherPos.getY(), catcherPos.getZ())) > 100) {
                return;
            }

            Entity entityByID = Minecraft.getInstance().level.getEntity(entityId);
            if (entityByID instanceof FallenStarEntity) {
                fsE = (FallenStarEntity) entityByID;
                fsE.toStarCatcher(catcherPos);
                fsE.setIsTargeted(true);

            } else {
                ManaMod.LOGGER.warn("setCatcher() called with invalid starCatcher: " + catcherPos);
            }
            ClientFallenStarData.setCatcherPos(catcherPos);
        });
        return true;
    }
}
