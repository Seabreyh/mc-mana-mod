package com.seabreyh.mana.content.mana_stat;

import net.minecraft.nbt.CompoundTag;

public class PlayerManaStat {

    private final int STARTING_MAX_MANA = 2;
    private final int MIN_MANA_VAL = 0;
    private final int ABSOLUTE_MAX_MANA_VAL = 20;

    private int mana_stat;
    private int mana_capacity = STARTING_MAX_MANA;

    public int getManaValue() {
        return mana_stat;
    }

    public int getManaCapacity() {
        return mana_capacity;
    }

    public boolean isFull() {
        return getManaValue() >= mana_capacity;
    }

    public boolean isAtMaxCapacity() {
        return getManaCapacity() >= ABSOLUTE_MAX_MANA_VAL;
    }

    public void addMana(int add) {
        this.mana_stat = Math.min(Math.min(mana_stat + add, mana_capacity), ABSOLUTE_MAX_MANA_VAL);
    }

    public void addManaCapacity(int add) {
        this.mana_capacity = Math.min(mana_capacity + add, ABSOLUTE_MAX_MANA_VAL);
    }

    public void subMana(int sub) {
        this.mana_stat = Math.max(mana_stat - sub, MIN_MANA_VAL);
    }

    public void copyFrom(PlayerManaStat source) {
        this.mana_stat = source.mana_capacity;
        this.mana_capacity = source.mana_capacity;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("mana", mana_stat);
        nbt.putInt("capacity", mana_capacity);
    }

    public void loadNBTData(CompoundTag nbt) {
        mana_stat = nbt.getInt("mana");
        mana_capacity = nbt.getInt("mana");
    }
}
