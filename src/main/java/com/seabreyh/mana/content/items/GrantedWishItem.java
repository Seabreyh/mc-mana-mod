package com.seabreyh.mana.content.items;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent.WishType;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class GrantedWishItem extends SealedWishItem {

    public static final Properties PROPERTIES = new Item.Properties().stacksTo(1);

    public GrantedWishItem(Properties p_41383_) {
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

            WishType wishType = SealedWishItem.getWishType(itemstack);
            switch (wishType) {
                case WEATHER_CLEAR:
                    // world.setThunderLevel(0f);
                    ((ServerLevel) world).setWeatherParameters(0, 0, false, false);
                    break;
                case WEATHER_STORM:
                    ((ServerLevel) world).setWeatherParameters(0, 0, true, true);

                    // world.setThunderLevel(1f);
                    break;
                case SUMMON_STAR_FRIEND:
                    WanderingTrader wTrader = new WanderingTrader(EntityType.WANDERING_TRADER, world);
                    wTrader.setPos(player.position());
                    world.addFreshEntity(wTrader);
                default:
                    break;
            }

            return InteractionResultHolder.consume(itemstack);
        }

        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        int wishTypeIndx = compoundtag.getInt("wishType");
        WishType wishType = PlayerWishEvent.fromIndex(wishTypeIndx);

        components
                .add(Component.translatable(PlayerWishEvent.displayName(wishType))
                        .withStyle(ChatFormatting.GRAY));

        components
                .add(Component.translatable(
                        "Right-click to use")
                        .withStyle(ChatFormatting.BLUE));

    }

    private void playSound(Level level, Player player) {
        RandomSource random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.BLOCKS, 10.25F,
                (random.nextFloat() - random.nextFloat()) * 0.1F + 1.5F);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

}
