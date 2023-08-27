package com.seabreyh.mana.foundation.mana_stat;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerManaStatProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerManaStat> PLAYER_MANA_STAT = CapabilityManager
            .get(new CapabilityToken<PlayerManaStat>() {
            });

    private PlayerManaStat mana_stat = null;
    private final LazyOptional<PlayerManaStat> optional = LazyOptional.of(this::createPlayerManaStat);

    private PlayerManaStat createPlayerManaStat() {
        if (this.mana_stat == null) {
            this.mana_stat = new PlayerManaStat();
        }

        return this.mana_stat;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MANA_STAT) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerManaStat().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerManaStat().loadNBTData(nbt);
    }
}
