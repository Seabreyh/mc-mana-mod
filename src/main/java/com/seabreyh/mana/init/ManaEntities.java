package com.seabreyh.mana.init;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.projectiles.AmethystEnergyBall;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaEntities {

        public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
                        ManaMod.MOD_ID);

        public static final RegistryObject<EntityType<AmethystEnergyBall>> AMETHYST_ENERGY_BALL = ENTITIES.register(
                        "amethyst_energy_ball",
                        () -> EntityType.Builder.<AmethystEnergyBall>of(AmethystEnergyBall::new, MobCategory.MISC)
                                        .build("amethyst_energy_ball"));
}
