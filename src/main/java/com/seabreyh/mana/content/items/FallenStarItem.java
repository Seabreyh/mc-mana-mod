package com.seabreyh.mana.content.items;

import javax.annotation.Nullable;
import java.util.List;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.FallenStarEntity;
import com.seabreyh.mana.foundation.event.player.PlayerManaEvent;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;

public class FallenStarItem extends Item {

    public static final Properties PROPERTIES = new Item.Properties();

    public FallenStarItem(Properties p_41383_) {
        super(p_41383_);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    // Use Block Entity model for Item model
    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) ManaMod.PROXY.getISTERProperties());
    }

    // Right click functionality
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player,
            InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean fullMana = false;
        if (!world.isClientSide) {
            // Handle regeneration of player mana from using star
            fullMana = PlayerManaEvent.regenMana(player, 1);

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

    // Make fuel source for furnace
    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 3200;
    }

    // Block Dispenser functionality
    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Level level = source.getLevel();
            Position position = DispenserBlock.getDispensePosition(source);
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Projectile projectile = this.getProjectile(level, position, stack, direction);
            projectile.shoot((double) direction.getStepX(), (double) ((float) direction.getStepY() + 0.1F),
                    (double) direction.getStepZ(), this.getPower(), this.getUncertainty());
            level.addFreshEntity(projectile);
            stack.shrink(1);
            return stack;
        }

        protected Projectile getProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
            FallenStarEntity fallenStar;
            if (direction != Direction.UP && direction != Direction.DOWN) {
                fallenStar = new FallenStarEntity(level, pos.x(), pos.y() - 0.4, pos.z());
            } else {
                fallenStar = new FallenStarEntity(level, pos.x(), pos.y(), pos.z());
            }

            fallenStar.pickup = AbstractArrow.Pickup.ALLOWED;
            return fallenStar;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.getLevel()
                    .levelEvent(1000, source.getPos(), 0);
        }

        protected float getUncertainty() {
            return 6.0F;
        }

        protected float getPower() {
            return 2.5F;
        }

    };

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.mana.fallen_star.tooltip"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.mana.lshift.tooltip"));
        }
    }
}
