package com.seabreyh.mana.client.renderers.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.seabreyh.mana.entity.FallenStar;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
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

    public boolean isFalling;
    public int age;

    public FallenStarSyncData() {
        isFalling = true;
    }

    public FallenStarSyncData copy() {
        return null;

    }

    public void write(FriendlyByteBuf buffer) {

    }

    public void read(FriendlyByteBuf buffer) {

    }

    public void update(FallenStar entity) {

    }

    public void apply(FallenStar fallenStar) {
    }

}
