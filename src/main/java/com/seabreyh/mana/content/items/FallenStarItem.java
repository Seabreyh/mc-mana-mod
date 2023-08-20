package com.seabreyh.mana.content.items;

// import com.seabreyh.mana.event.player.PlayerManaEvent;

import java.util.Random;
import javax.annotation.Nullable;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.client.renderers.item.ManaItemStackRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class FallenStarItem extends Item {

    public FallenStarItem(Properties p_41383_) {
        super(p_41383_);
    }

    // Use Block Entity model for Item model
    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) ManaMod.PROXY.getISTERProperties());
    }

    @Override
    // Called when player right clicks star
    public InteractionResultHolder<ItemStack> use(Level world, Player player,
            InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean fullMana = false;
        if (!world.isClientSide) {
            // Handle regeneration of player mana from using star
            // fullMana = PlayerManaEvent.regenMana(player, 1);

            if (fullMana) {
                return InteractionResultHolder.pass(itemstack);

            } else {
                this.playSound(world, player);
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    player.getInventory().removeItem(itemstack);

                }
                return InteractionResultHolder.consume(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

    private void playSound(Level level, Player player) {
        RandomSource random = level.getRandom();
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.BLOCKS, 0.25F,
                (random.nextFloat() - random.nextFloat()) * 0.2F + 1.5F);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 3200;
    }
}
