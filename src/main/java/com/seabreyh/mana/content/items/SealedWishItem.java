package com.seabreyh.mana.content.items;

import java.util.List;
import javax.annotation.Nullable;

import com.seabreyh.mana.foundation.event.player.PlayerWishEvent;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent.WishType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SealedWishItem extends Item {

    public static final Properties PROPERTIES = new Item.Properties().stacksTo(1);

    public SealedWishItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        CompoundTag compoundtag = stack.getOrCreateTag();
        int wishTypeIndx = compoundtag.getInt("wishType");
        WishType wishType = PlayerWishEvent.fromIndex(wishTypeIndx);

        if (Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.mana.sealed_wish_item.tooltip"));
        } else {
            components.add(Component.translatable(PlayerWishEvent.displayName(wishType)));
            components.add(Component.translatable("tooltip.mana.lshift.tooltip"));
        }
    }

    public static void addWishType(ItemStack itemstack, WishType wish) {
        CompoundTag compoundtag = itemstack.getOrCreateTag();
        compoundtag.putInt("wishType", wish.ordinal());
    }

    public static WishType getWishType(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        int wishTypeIndx = compoundtag.getInt("wishType");
        WishType wishType = PlayerWishEvent.fromIndex(wishTypeIndx);
        return wishType;
    }

}
