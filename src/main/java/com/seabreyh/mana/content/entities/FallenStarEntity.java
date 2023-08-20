package com.seabreyh.mana.content.entities;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.registries.ManaItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FallenStarEntity extends AbstractStarEntity {

    public FallenStarEntity(EntityType<? extends FallenStarEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public FallenStarEntity(EntityType<? extends FallenStarEntity> p_19870_, Level p_19871_, Player player) {
        super(p_19870_, p_19871_, player);
    }

    public void tick() {
        super.tick();
    }

    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

}