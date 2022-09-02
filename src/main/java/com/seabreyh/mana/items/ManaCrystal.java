package com.seabreyh.mana.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.event.player.PlayerManaEvent;
import com.seabreyh.mana.event.player.PlayerWishEvent.WishType;
import com.seabreyh.mana.mana_stat.PlayerManaStatProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.seabreyh.mana.event.player.PlayerWishEvent;

public class ManaCrystal extends Item {

    public ManaCrystal(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            this.playSound(world, player);

            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                player.getInventory().removeItem(itemstack);
            }

            player.getCapability(PlayerManaStatProvider.PLAYER_MANA_STAT).ifPresent(mana_stat -> {
                PlayerManaEvent.increaseManaCapacity(player, 1);
            });

            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    private void playSound(Level level, Player player) {
        Random random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.BLOCKS, 10.25F,
                (random.nextFloat() - random.nextFloat()) * 0.1F + 1.5F);
    }

    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components
                .add(new TranslatableComponent(
                        "Right-click to increase your maximum Mana")
                        .withStyle(ChatFormatting.BLUE));
    }

}
