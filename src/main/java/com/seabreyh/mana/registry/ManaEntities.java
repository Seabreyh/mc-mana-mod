package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.AmethystEnergyBall;
import com.seabreyh.mana.entity.EmeraldEnergyBall;
import com.seabreyh.mana.entity.FallenStar;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaEntities {

        public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
                        ForgeRegistries.ENTITY_TYPES,
                        ManaMod.MOD_ID);

        public static final RegistryObject<EntityType<AmethystEnergyBall>> AMETHYST_ENERGY_BALL = ENTITIES.register(
                        "amethyst_energy_ball",
                        () -> EntityType.Builder.<AmethystEnergyBall>of(AmethystEnergyBall::new,
                                        MobCategory.MISC)
                                        .sized(0.3f, 0.3f).build("amethyst_energy_ball"));

        public static final RegistryObject<EntityType<EmeraldEnergyBall>> EMERALD_ENERGY_BALL = ENTITIES.register(
                        "emerald_energy_ball",
                        () -> EntityType.Builder.<EmeraldEnergyBall>of(EmeraldEnergyBall::new, MobCategory.MISC)
                                        .sized(0.3f, 0.3f).build("emerald_energy_ball"));

        public static final RegistryObject<EntityType<FallenStar>> FALLEN_STAR = ENTITIES.register(
                        "fallen_star",
                        () -> EntityType.Builder.<FallenStar>of(FallenStar::new, MobCategory.MISC)
                                        .sized(0.5f, 0.7f).build("fallen_star"));

        // public static final RegistryObject<EntityType<Meteor>> METEOR =
        // ENTITIES.register(
        // "meteor",
        // () -> EntityType.Builder.<Meteor>of(Meteor::new, MobCategory.MISC)
        // .build("meteor"));

        // public static Object CAUGHT_STAR;

        // public static final RegistryObject<EntityType<FallenStar>> CAUGHT_STAR =
        // ENTITIES.register(
        // "caught_star",
        // () -> EntityType.Builder.<FallenStar>of(FallenStar::new, MobCategory.MISC)
        // .build("caught_star"));
}
