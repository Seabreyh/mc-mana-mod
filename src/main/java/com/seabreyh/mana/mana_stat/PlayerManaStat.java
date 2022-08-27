package com.seabreyh.mana.mana_stat;

import net.minecraft.nbt.CompoundTag;

public class PlayerManaStat {
    private int mana_stat;
    private final int MIN_MANA_VAL = 0;
    private final int MAX_MANA_VAL = 10;

    public int getManaValue() {
        return mana_stat;
    }

    public boolean isFull() {
        return getManaValue() >= MAX_MANA_VAL;
    }

    public void addMana(int add) {
        this.mana_stat = Math.min(mana_stat + add, MAX_MANA_VAL);
    }

    public void subMana(int sub) {
        this.mana_stat = Math.max(mana_stat - sub, MIN_MANA_VAL);
    }

    public void copyFrom(PlayerManaStat source) {
        this.mana_stat = source.mana_stat;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("mana", mana_stat);
    }

    public void loadNBTData(CompoundTag nbt) {
        mana_stat = nbt.getInt("mana");
    }
}
