package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.AmethystStaffProjectile;
import com.seabreyh.mana.content.entities.EmeraldStaffProjectile;
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

        public static final RegistryObject<EntityType<AmethystStaffProjectile>> AMETHYST_ENERGY_BALL = ENTITIES
                        .register(
                                        "amethyst_energy_ball",
                                        () -> EntityType.Builder
                                                        .<AmethystStaffProjectile>of(AmethystStaffProjectile::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.3f, 0.3f).build("amethyst_energy_ball"));

        public static final RegistryObject<EntityType<EmeraldStaffProjectile>> EMERALD_ENERGY_BALL = ENTITIES.register(
                        "emerald_energy_ball",
                        () -> EntityType.Builder
                                        .<EmeraldStaffProjectile>of(EmeraldStaffProjectile::new, MobCategory.MISC)
                                        .sized(0.3f, 0.3f).build("emerald_energy_ball"));

        public static final RegistryObject<EntityType<FallenStarEntity>> FALLEN_STAR = ENTITIES.register(
                        "fallen_star",
                        () -> EntityType.Builder.<FallenStarEntity>of(FallenStarEntity::new, MobCategory.MISC)
                                        .sized(0.5f, 0.7f).build("fallen_star"));

        // public static final RegistryObject<EntityType<Meteor>> METEOR =
        // ENTITIES.register(
        // "meteor",
        // () -> EntityType.Builder.<Meteor>of(Meteor::new, MobCategory.MISC)
        // .build("meteor"));

        public static void register(IEventBus eventBus) {
                ENTITIES.register(eventBus);
        }

}
