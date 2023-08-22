package com.seabreyh.mana.foundation.event;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.AbstractStarEntity;
import com.seabreyh.mana.content.entities.StarImpactEvent;
import com.seabreyh.mana.foundation.event.player.PlayerManaEvent;
import com.seabreyh.mana.foundation.event.world.ShootingStarEvent;
import com.seabreyh.mana.foundation.mana_stat.PlayerManaStat;
import com.seabreyh.mana.foundation.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.networking.packet.ManaStatSyncS2CPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID)
public class ManaEvents {

    @SubscribeEvent
    public static void onPlayerEvent(final LivingTickEvent event) {
        ShootingStarEvent.processPlayerEvent(event);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).isPresent()) {
                event.addCapability(new ResourceLocation(ManaMod.MOD_ID, "properties"), new PlayerManaStatProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        // if (event.getEntity().isClientSide()) {
        // return;
        // }
        event.getOriginal().reviveCaps();
        if (event.isWasDeath()) {

            event.getOriginal().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(oldStore -> {

                event.getEntity().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerManaStat.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {

            // Constantly regenerates mana for each player at a slow rate
            event.player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                if (event.player.tickCount % 110 == 0) {
                    PlayerManaEvent.regenMana(event.player, 1);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                    ManaMessages.sendToPlayer(
                            new ManaStatSyncS2CPacket(mana_stat.getManaValue(), mana_stat.getManaCapacity()), player);
                });
            }
        }
    }

}