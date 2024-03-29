package com.seabreyh.mana.content.items;

import com.seabreyh.mana.foundation.event.player.PlayerManaEvent;
import com.seabreyh.mana.foundation.mana_stat.PlayerManaStatProvider;
import com.seabreyh.mana.registries.ManaEffects;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ManaTreat extends Item {

    public static final Properties PROPERTIES = (new Item.Properties()).food((new FoodProperties.Builder())
            .nutrition(2)
            .saturationMod(0.2f)
            .build());

    boolean isAtMaxManaLevel;

    public ManaTreat(Properties p_41383_) {
        super(p_41383_);
    }

    // ------------------ Functions ------------------

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player,
            InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean fullMana = false;
        if (!world.isClientSide) {
            // Handle regeneration of player mana from using star
            fullMana = PlayerManaEvent.isAtMaxManaLevel(player);

            if (fullMana) {
                return InteractionResultHolder.pass(itemstack);

            } else {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world,
            LivingEntity livingEntity) {
        if (livingEntity instanceof Player) {
            if (!world.isClientSide) {
                // play mana treat effects
                this.playSound(world, ((Player) livingEntity));

                ((ServerLevel) world).sendParticles(ParticleTypes.FLASH, livingEntity.getX(),
                        livingEntity.getY(), livingEntity.getZ(), 1,
                        0D,
                        0D, 0D, 0D);
                ((ServerLevel) world).sendParticles(ParticleTypes.END_ROD,
                        livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                        20,
                        1D, 1D, 1D, 0.3D);

                ((ServerLevel) world).sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                        20,
                        1D, 1D, 1D, 0.3D);

                // call event packet
                ((Player) livingEntity).getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                    PlayerManaEvent.increaseManaCapacity(((Player) livingEntity), 1);
                });

                return this.isEdible() ? livingEntity.eat(world, itemStack) : itemStack;

            }
        }
        return this.isEdible() ? livingEntity.eat(world, itemStack) : itemStack;
    }

    private void playSound(Level level, Player player) {
        RandomSource random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.BLOCKS, 5F,
                (random.nextFloat() - random.nextFloat()) * 0.1F + 1.5F);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.mana.manatreat.tooltip"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.mana.lshift.tooltip"));
        }
    }

}
