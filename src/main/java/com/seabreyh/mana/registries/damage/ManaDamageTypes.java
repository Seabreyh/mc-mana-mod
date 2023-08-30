package com.seabreyh.mana.registries.damage;

import com.seabreyh.mana.ManaMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public class ManaDamageTypes {
    public static final DamageTypeData FALLEN_STAR = DamageTypeData.builder()
            .simpleId("fallen_star")
            .tag(DamageTypeTags.BYPASSES_ARMOR)
            .build();

    // public static final DamageTypeData TEST_DAMAGE = DamageTypeData.builder()
    // .simpleId("test_damage")
    // .exhaustion(0.1f)
    // .scaling(DamageScaling.ALWAYS)
    // .tag(DamageTypeTags.IS_EXPLOSION)
    // .build();

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        DamageTypeData.allInNamespace(ManaMod.MOD_ID).forEach(data -> data.register(ctx));
    }
}
