package com.seabreyh.mana.foundation.client.renderers.entities;

import com.seabreyh.mana.content.blocks.block_entities.StarCatcherBlockEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class FallenStarSyncData {
    public boolean noPhysics;
    public boolean inGround;
    public AbstractArrow.Pickup pickup;
    public boolean isTargeted;
    public StarCatcherBlockEntity catcher;
    public boolean moveToCatcher;

    public FallenStarSyncData() {
        noPhysics = false;
    }

    // public void setNoPhysics(boolean bool) {
    // this.noPhysics = bool;
    // }

    public FallenStarSyncData copy() {
        FallenStarSyncData data = new FallenStarSyncData();
        data.noPhysics = noPhysics;
        return data;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(noPhysics);
    }

    public StarCatcherBlockEntity read(FriendlyByteBuf buffer) {
        return catcher;
    }

}
