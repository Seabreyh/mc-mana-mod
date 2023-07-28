package com.seabreyh.mana.items;

import com.seabreyh.mana.event.player.PlayerManaEvent;
import com.seabreyh.mana.mana_stat.PlayerManaStatProvider;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ManaTreat extends Item {

    public ManaTreat(Properties p_41383_) {
        super(p_41383_);
    }

    public static final FoodProperties MANA_TREAT = (new FoodProperties.Builder()).fast().nutrition(2)
            .saturationMod(0.2F)
            .build();

    // @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity livingEntity) {
        if (livingEntity instanceof Player) {
            if (!world.isClientSide) {
                this.playSound(world, ((Player) livingEntity));

                ((Player) livingEntity).getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                    PlayerManaEvent.increaseManaCapacity(((Player) livingEntity), 1);
                });
                itemStack.shrink(1);
            }
        }
        return livingEntity instanceof Player && ((Player) livingEntity).getAbilities().instabuild ? itemStack
                : itemStack;
    }

    private void playSound(Level level, Player player) {
        Random random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.BLOCKS, 10.25F,
                (random.nextFloat() - random.nextFloat()) * 0.1F + 1.5F);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {

        pTooltipComponents.add(new TranslatableComponent("tooltip.mana.manatreat.tooltip"));

    }

}
