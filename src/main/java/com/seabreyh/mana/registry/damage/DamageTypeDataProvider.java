package com.seabreyh.mana.registry.damage;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.seabreyh.mana.registry.damage.ManaDamageTypes;

import com.seabreyh.mana.ManaMod;

import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class DamageTypeDataProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ManaDamageTypes::bootstrap);

    public DamageTypeDataProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, BUILDER, Set.of(ManaMod.MOD_ID));
    }

    public static DataProvider.Factory<DamageTypeDataProvider> makeFactory(CompletableFuture<Provider> registries) {
        return output -> new DamageTypeDataProvider(output, registries);
    }

    @Override
    @NotNull
    public String getName() {
        return "ManaMods's Damage Type Data";
    }
}