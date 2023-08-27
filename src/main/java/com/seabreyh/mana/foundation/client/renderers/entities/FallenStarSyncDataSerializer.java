package com.seabreyh.mana.foundation.client.renderers.entities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class FallenStarSyncDataSerializer implements EntityDataSerializer<FallenStarSyncData> {
    @Override
    public void write(FriendlyByteBuf buffer, FallenStarSyncData data) {
        data.write(buffer);
    }

    @Override
    public FallenStarSyncData read(FriendlyByteBuf buffer) {
        FallenStarSyncData data = new FallenStarSyncData();
        data.read(buffer);
        return data;
    }

    @Override
    public FallenStarSyncData copy(FallenStarSyncData data) {
        return data.copy();
    }

}
