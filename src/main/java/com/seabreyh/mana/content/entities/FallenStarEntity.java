package com.seabreyh.mana.content.entities;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.registries.ManaEntities;
import com.seabreyh.mana.registries.ManaItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class FallenStarEntity extends AbstractStarEntity {

    public FallenStarEntity(EntityType<? extends FallenStarEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);

    }

    public FallenStarEntity(EntityType<? extends FallenStarEntity> p_19870_, Level p_19871_, Player player) {
        super(p_19870_, p_19871_, player);
    }

    public FallenStarEntity(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(ManaEntities.FALLEN_STAR.get(), p_36862_, p_36863_, p_36864_, p_36861_);
    }

    public void tick() {
        super.tick();
    }

    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

}