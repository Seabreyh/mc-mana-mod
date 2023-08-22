package com.seabreyh.mana.foundation.client.renderers.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.seabreyh.mana.content.blocks.block_entities.BlockEntityStarCatcher;
import com.seabreyh.mana.content.entities.AbstractStarEntity;
import com.seabreyh.mana.content.entities.FallenStarEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.mutable.MutableBoolean;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FallenStarSyncData {
    public boolean noPhysics;
    public boolean inGround;
    public AbstractArrow.Pickup pickup;
    public boolean isTargeted;
    public BlockEntityStarCatcher catcher;
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

    public BlockEntityStarCatcher read(FriendlyByteBuf buffer) {
        return catcher;
    }

}
