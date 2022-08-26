package com.seabreyh.mana.world;

import java.util.Random;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.FallenStar;
import com.seabreyh.mana.registry.ManaEntities;
import com.seabreyh.mana.world.events.ShootingStarEvent;
import com.seabreyh.mana.world.gen.ManaEntityGeneration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult.Double;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID)
public class ManaWorldEvents {

    @SubscribeEvent
    public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
        ManaEntityGeneration.onEntitySpawn(event);
    }

    @SubscribeEvent
    public static void onPlayerEvent(final LivingUpdateEvent event) {
        ShootingStarEvent.processPlayerEvent(event);
    }
}