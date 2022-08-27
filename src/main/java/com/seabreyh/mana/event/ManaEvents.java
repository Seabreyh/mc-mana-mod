package com.seabreyh.mana.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.event.player.PlayerManaEvent;
import com.seabreyh.mana.event.world.ShootingStarEvent;
import com.seabreyh.mana.mana_stat.PlayerManaStat;
import com.seabreyh.mana.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.networking.ManaMessages;
import com.seabreyh.mana.networking.packet.ManaStatSyncS2CPacket;
import com.seabreyh.mana.registry.ManaItems;

import net.minecraft.world.level.Level;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID)
public class ManaEvents {

    @SubscribeEvent
    public static void onPlayerEvent(final LivingUpdateEvent event) {
        ShootingStarEvent.processPlayerEvent(event);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).isPresent()) {
                ManaMod.LOGGER.debug("###addCapability");
                event.addCapability(new ResourceLocation(ManaMod.MOD_ID, "properties"), new PlayerManaStatProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(newStore -> {
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

            // Constantly regenerates mana for each player at a slow, random rate
            event.player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                if (event.player.getRandom().nextFloat() < 0.005f) {
                    PlayerManaEvent.regenMana(event.player, 1);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                    ManaMod.LOGGER.debug("###SENDTOPLAYER onPlayerJoinWorld");
                    ManaMessages.sendToPlayer(new ManaStatSyncS2CPacket(mana_stat.getManaValue()), player);
                });
            }
        }
    }
}