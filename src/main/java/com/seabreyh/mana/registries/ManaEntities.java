package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.AmethystEnergyBall;
import com.seabreyh.mana.content.entities.EmeraldEnergyBall;
import com.seabreyh.mana.content.entities.FallenStarEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
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

        public static final RegistryObject<EntityType<FallenStarEntity>> FALLEN_STAR = ENTITIES.register(
                        "fallen_star",
                        () -> EntityType.Builder.<FallenStarEntity>of(FallenStarEntity::new, MobCategory.MISC)
                                        .sized(0.5f, 0.7f).build("fallen_star"));

        // public static final RegistryObject<EntityType<FallenStar>> FALLEN_STAR =
        // ENTITIES.register(
        // "fallen_star", FallenStar::new, () -> FallenStarRenderer::new,
        // MobCategory.MISC).sized(0.5f,
        // 0.7f)
        // .build("fallen_star");

        // public static final RegistryObject<EntityType<Meteor>> METEOR =
        // ENTITIES.register(
        // "meteor",
        // () -> EntityType.Builder.<Meteor>of(Meteor::new, MobCategory.MISC)
        // .build("meteor"));

        // public static Object CAUGHT_STAR;

        public static final RegistryObject<EntityType<FallenStarEntity>> CAUGHT_STAR = ENTITIES.register(
                        "caught_star",
                        () -> EntityType.Builder.<FallenStarEntity>of(FallenStarEntity::new, MobCategory.MISC)
                                        .build("caught_star"));

        public static void register(IEventBus eventBus) {
                ENTITIES.register(eventBus);
        }
}
